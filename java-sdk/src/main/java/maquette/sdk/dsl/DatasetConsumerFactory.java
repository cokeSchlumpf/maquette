package maquette.sdk.dsl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;

import com.fasterxml.jackson.databind.ObjectMapper;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;
import maquette.sdk.databind.AvroDeserializer;
import maquette.sdk.databind.AvroSerializer;
import maquette.sdk.databind.ObjectMapperFactory;
import maquette.sdk.util.MaquetteConfiguration;
import maquette.sdk.util.MaquetteRequestException;
import maquette.sdk.util.Operators;
import maquette.sdk.util.PublishDatasetVersionRequest;
import maquette.sdk.util.Records;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Wither
@AllArgsConstructor(staticName = "apply")
public final class DatasetConsumerFactory {

    private MaquetteConfiguration maquette;

    private ObjectMapper om;

    private OkHttpClient client;

    private int batchSize;

    public static DatasetConsumerFactory apply() {
        return apply(MaquetteConfiguration.apply(), ObjectMapperFactory.apply().create(), new OkHttpClient(), 100);
    }

    public <T> Source<T, NotUsed> createSource(String namespace, String dataset, String version, AvroDeserializer<T> deserializer) {
        Request request = maquette
            .createRequestFor("/datasets/%s/%s/versions/%s/data", namespace, dataset, version)
            .get()
            .build();

        Response response = Operators.suppressExceptions(() -> client.newCall(request).execute());
        ResponseBody body = response.body();


        if (response.isSuccessful() && body != null) {
            return Operators.suppressExceptions(() -> {
                final DatumReader<GenericData.Record> datumReader = new GenericDatumReader<>();
                final DataFileStream<GenericData.Record> dataFileStream = new DataFileStream<>(body.byteStream(), datumReader);

                return Source
                    .from(dataFileStream)
                    .grouped(batchSize)
                    .map(Records::apply)
                    .map(deserializer::mapRecords)
                    .mapConcat(list -> list);
            });
        } else {
            throw MaquetteRequestException.apply(request, response);
        }
    }

}
