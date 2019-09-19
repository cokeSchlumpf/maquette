package maquette.sdk.util;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.util.ByteBufferOutputStream;

import com.google.common.collect.ImmutableList;

import akka.util.ByteString;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Records {
    private final List<GenericData.Record> records;

    public static Records apply(List<GenericData.Record> records) {
        Schema s = null;

        for (GenericData.Record record : records) {
            if (s != null && !s.equals(record.getSchema())) {
                throw new IllegalArgumentException("All records must have the same schema!");
            }

            s = record.getSchema();
        }

        return new Records(ImmutableList.copyOf(records));
    }

    public Schema getSchema() {
        if (records.size() > 0) {
            return records.get(0).getSchema();
        } else {
            return SchemaBuilder.builder().nullType();
        }
    }

    public List<GenericData.Record> getRecords() {
        return records;
    }

    public List<ByteString> getBytes() {
        return Operators.suppressExceptions(() -> {
            ByteBufferOutputStream os = new ByteBufferOutputStream();
            writeToOutputStream(os);

            return ImmutableList
                .copyOf(os
                            .getBufferList()
                            .stream()
                            .map(ByteString::fromByteBuffer)
                            .collect(Collectors.toList()));
        });
    }

    public int size() {
        return records.size();
    }

    public void toFile(Path file) {
        Operators.suppressExceptions(() -> {
            try (OutputStream os = Files.newOutputStream(file)) {
                writeToOutputStream(os);
            }
        });
    }

    public void writeToOutputStream(OutputStream os) {
        Operators.suppressExceptions(() -> {
            DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(getSchema());
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(os, null);

            DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);
            dataFileWriter.create(getSchema(), os);

            for (GenericData.Record record : records) {
                dataFileWriter.append(record);
            }

            dataFileWriter.flush();
            dataFileWriter.close();
        });
    }

}
