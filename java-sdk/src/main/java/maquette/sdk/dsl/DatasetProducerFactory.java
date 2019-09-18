package maquette.sdk.dsl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import akka.Done;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;
import maquette.sdk.databind.AvroSerializer;
import maquette.sdk.databind.JacksonAvroSerializer;
import maquette.sdk.databind.ObjectMapperFactory;
import maquette.sdk.util.MaquetteConfiguration;
import maquette.sdk.util.Operators;
import maquette.sdk.util.PublishDatasetVersionRequest;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Wither
@AllArgsConstructor(staticName = "apply")
public final class DatasetProducerFactory {

    private MaquetteConfiguration maquette;

    private ObjectMapper om;

    private OkHttpClient client;

    private int batchSize;

    public static DatasetProducerFactory apply() {
        return apply(MaquetteConfiguration.apply(), ObjectMapperFactory.apply().create(), new OkHttpClient(), 100);
    }

    public <T> Flow<T, T, CompletionStage<Done>> createFlow(String namespace, String dataset, Class<T> recordType) {
        return createFlow(
            namespace,
            dataset,
            JacksonAvroSerializer.apply(ObjectMapperFactory.apply().createAvroMapper(), recordType));
    }

    public <T> Flow<T, T, CompletionStage<Done>> createFlow(String namespace, String dataset, AvroSerializer<T> serializer) {
        CompletableFuture<CreatedDatasetVersionRequest<T>> created = new CompletableFuture<>();

        return Flow
            .of(serializer.getRecordType())
            .grouped(batchSize)
            .map(DatasetRequest::apply)
            .prepend(Source.single(1).map(i -> createDatasetVersion(namespace, dataset, serializer)))
            .map(request -> {
                if (request instanceof CreatedDatasetVersionRequest) {
                    created.complete((CreatedDatasetVersionRequest<T>) request);
                    return Lists.<T>newArrayList();
                } else if (request instanceof PushRecordsRequest) {
                    String uid = Operators.suppressExceptions(() -> created.get().getUid());
                    pushRecords(namespace, dataset, ((PushRecordsRequest<T>) request).records, uid, serializer);
                    return ((PushRecordsRequest<T>) request).getRecords();
                } else {
                    return Lists.<T>newArrayList();
                }
            })
            .mapConcat(list -> list)
            .watchTermination(Keep.right())
            .mapMaterializedValue(done -> done.thenCompose(d -> created.thenApply(c -> {
                publishDatasetVersion(c.getUid(), namespace, dataset, serializer);
                return Done.getInstance();
            })));
    }

    public <T> Sink<T, CompletionStage<Done>> createSink(String namespace, String dataset, Class<T> recordType) {
        return createSink(
            namespace, dataset,
            JacksonAvroSerializer.apply(ObjectMapperFactory.apply().createAvroMapper(), recordType));
    }

    public <T> Sink<T, CompletionStage<Done>> createSink(String namespace, String dataset, AvroSerializer<T> serializer) {
        CompletableFuture<CreatedDatasetVersionRequest<T>> created = new CompletableFuture<>();

        return Flow
            .of(serializer.getRecordType())
            .viaMat(createFlow(namespace, dataset, serializer), Keep.right())
            .toMat(Sink.ignore(), Keep.left());
    }

    private <T> void pushRecords(
        String namespace, String dataset,
        List<T> records, String uid, AvroSerializer<T> serializer) {

        Path tmp = Operators.suppressExceptions(() -> Files.createTempFile("mq", "records"));
        serializer.mapRecords(records).toFile(tmp);

        RequestBody requestBody = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", "file",
                RequestBody.create(tmp.toFile(), MediaType.parse("avro/binary")))
            .build();

        Request request = maquette
            .createRequestFor("/datasets/%s/%s/versions/%s", namespace, dataset, uid)
            .patch(requestBody)
            .build();

        Response response = Operators.suppressExceptions(() -> client.newCall(request).execute());

        if (!response.isSuccessful()) {
            System.out.println(response);
            throw new RuntimeException("uuuups");
        }
    }

    private <T> CreatedDatasetVersionRequest<T> createDatasetVersion(
        String namespace, String dataset, AvroSerializer<T> serializer) {

        Request request = maquette
            .createRequestFor("/datasets/%s/%s/versions", namespace, dataset)
            .post(RequestBody.create(
                serializer.getSchema().toString(true),
                MediaType.parse("application/json; charset=utf-8")))
            .build();

        Response response = Operators.suppressExceptions(() -> client.newCall(request).execute());

        return Operators.suppressExceptions(() -> {
            ResponseBody body = response.body();

            if (response.isSuccessful() && body != null) {
                String uid = om.readValue(body.string(), String.class);
                return CreatedDatasetVersionRequest.apply(uid);
            } else {
                throw new RuntimeException("ups");
            }
        });
    }

    private <T> void publishDatasetVersion(
        String uid, String namespace, String dataset, AvroSerializer<T> serializer) {

        Operators.suppressExceptions(() -> {
            Request request = maquette
                .createRequestFor("/datasets/%s/%s/versions/%s", namespace, dataset, uid)
                .post(RequestBody.create(
                    om.writeValueAsBytes(PublishDatasetVersionRequest.apply("Published from Java")),
                    MediaType.parse("application/json")))
                .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                System.out.println(response);
                System.out.println(response.body().string());
                throw new RuntimeException("ups");
            }
        });
    }

    private interface DatasetRequest<T> {

        static <T> DatasetRequest<T> apply(List<T> records) {
            return PushRecordsRequest.apply(records);
        }

    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    private static final class CreatedDatasetVersionRequest<T> implements DatasetRequest<T> {

        private final String uid;

    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    private static final class PushRecordsRequest<T> implements DatasetRequest<T> {

        private final List<T> records;

    }

}
