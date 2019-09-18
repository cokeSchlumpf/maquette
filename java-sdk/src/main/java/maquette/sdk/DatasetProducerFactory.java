package maquette.sdk;

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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Wither
@AllArgsConstructor(staticName = "apply")
public final class DatasetProducerFactory<T> {

    private String baseUrl;

    private String user;

    private String token;

    private String namespace;

    private String dataset;

    private int batchSize;

    private AvroSerializer<T> serializer;

    private ObjectMapper om;

    private OkHttpClient client;

    public Sink<T, CompletionStage<Done>> build() {
        CompletableFuture<CreatedDatasetVersionRequest<T>> created = new CompletableFuture<>();

        return Flow
            .of(serializer.getRecordType())
            .grouped(batchSize)
            .map(DatasetRequest::apply)
            .prepend(Source.single(1).map(i -> createDatasetVersion()))
            .map(request -> {
                if (request instanceof CreatedDatasetVersionRequest) {
                    created.complete((CreatedDatasetVersionRequest<T>) request);
                    return Lists.newArrayList();
                } else if (request instanceof PushRecordsRequest) {
                    String uid = Operators.suppressExceptions(() -> created.get().getUid());
                    pushRecords(((PushRecordsRequest<T>) request).records, uid);
                    return ((PushRecordsRequest<T>) request).getRecords();
                } else {
                    return Lists.newArrayList();
                }
            })
            .toMat(Sink.ignore(), (left, right) -> right
                .thenCompose(done -> created)
                .thenApply(c -> {
                    publishDatasetVersion(c.getUid());
                    return Done.getInstance();
                }));
    }

    private void pushRecords(List<T> records, String uid) {
        Path tmp = Operators.suppressExceptions(() -> Files.createTempFile("mq", "records"));
        serializer.mapRecords(records).toFile(tmp);

        RequestBody requestBody = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", "file",
                RequestBody.create(tmp.toFile(), MediaType.parse("avro/binary")))
            .build();

        Request request = new Request.Builder()
            .url(String.format("%s/datasets/%s/%s/versions/%s", baseUrl, namespace, dataset, uid))
            .header("x-user-id", user)
            // .header("x-user-token", token)
            .patch(requestBody)
            .build();

        Response response = Operators.suppressExceptions(() -> client.newCall(request).execute());

        if (!response.isSuccessful()) {
            System.out.println(response);
            throw new RuntimeException("uuuups");
        }
    }

    private CreatedDatasetVersionRequest<T> createDatasetVersion() {
        Request request = new Request.Builder()
            .url(String.format("%s/datasets/%s/%s/versions", baseUrl, namespace, dataset))
            .header("x-user-id", user)
            // .header("x-user-token", token)
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

    private void publishDatasetVersion(String uid) {
        Operators.suppressExceptions(() -> {
            Request request = new Request.Builder()
                .url(String.format("%s/datasets/%s/%s/versions/%s", baseUrl, namespace, dataset, uid))
                .header("x-user-id", user)
                // .header("x-user-token", token)
                .post(RequestBody.create(
                    om.writeValueAsBytes(PublishDatasetVersionRequest.apply("Published from Java")),
                    MediaType.parse("application/json")))
                .build();

            Response response = Operators.suppressExceptions(() -> client.newCall(request).execute());

            if (!response.isSuccessful()) {
                System.out.println(response);
                System.out.println(response.body().string());
                throw new RuntimeException("ups");
            }
        });
    }

    private interface DatasetRequest<T> {

        static <T> DatasetRequest<T> apply(String uid) {
            return CreatedDatasetVersionRequest.apply(uid);
        }

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
