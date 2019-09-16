package maquette.controller.domain.values.core.records;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.util.ByteBufferInputStream;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import akka.util.ByteString;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.exceptions.InvalidAvroFileException;

@JsonSerialize(using = RecordsSerializer.class)
@JsonDeserialize(using = RecordsDeserializer.class)
public interface Records {

    static Records empty() {
        return AvroRecords.apply(Lists.newArrayList());
    }

    static Records fromRecords(List<GenericData.Record> records) {
        return AvroRecords.apply(records);
    }

    static Records fromByteBuffers(List<ByteBuffer> data) {
        return fromByteStrings(data.stream().map(ByteString::fromByteBuffer).collect(Collectors.toList()));
    }

    static Records fromByteStrings(List<ByteString> data) {
        return new EncodedAvroRecords(ImmutableList.copyOf(data));
    }

    static Records fromFile(Path file) {
        try {
            try (InputStream is = Files.newInputStream(file)) {
                DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
                DataFileStream<GenericRecord> dataFileStream = new DataFileStream<>(is, datumReader);

                List<GenericData.Record> records = Lists
                    .newArrayList(dataFileStream.iterator())
                    .stream()
                    .map(record -> (GenericData.Record) record)
                    .collect(Collectors.toList());

                return AvroRecords.apply(records);
            }
        } catch (Exception e) {
            throw InvalidAvroFileException.apply(e);
        }
    }

    Schema getSchema();

    List<GenericData.Record> getRecords();

    List<ByteString> getBytes();

    int size();

}
