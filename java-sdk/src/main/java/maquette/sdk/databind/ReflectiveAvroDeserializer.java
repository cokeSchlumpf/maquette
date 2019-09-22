package maquette.sdk.databind;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.util.ByteBufferInputStream;

import com.google.common.collect.Lists;

import akka.util.ByteString;
import lombok.AllArgsConstructor;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.core.records.Records;

@AllArgsConstructor(staticName = "apply")
public final class ReflectiveAvroDeserializer<T> implements AvroDeserializer<T> {

    private final Class<?> model;

    @Override
    public Class<T> getRecordType() {
        return null;
    }

    @Override
    public Iterable<T> mapRecords(Records records) {
        return Operators.suppressExceptions(() -> {
            final Schema schema = ReflectData.get().getSchema(model);
            final ReflectDatumReader<T> reader = new ReflectDatumReader<>(schema);

            final List<ByteBuffer> bytes = records
                .getBytes()
                .stream()
                .map(ByteString::asByteBuffer)
                .collect(Collectors.toList());

            final ByteBufferInputStream is = new ByteBufferInputStream(bytes);
            final DataFileStream<T> dataFileStream = new DataFileStream<>(is, reader);

            return Lists.newArrayList(dataFileStream.iterator());
        });
    }

}
