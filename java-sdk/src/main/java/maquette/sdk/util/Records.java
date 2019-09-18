package maquette.sdk.util;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.util.ByteBufferOutputStream;

import com.google.common.collect.ImmutableList;

import akka.util.ByteString;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Records {

    private final Schema schema;

    private final List<GenericData.Record> records;

    public static Records apply(Schema schema, List<GenericData.Record> records) {
        return new Records(schema, ImmutableList.copyOf(records));
    }

    public Schema getSchema() {
        return schema;
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

    private void writeToOutputStream(OutputStream os) {
        Operators.suppressExceptions(() -> {
            DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(getSchema());
            DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);
            dataFileWriter.create(getSchema(), os);

            for (GenericData.Record record : records) {
                dataFileWriter.append(record);
            }

            dataFileWriter.close();
        });
    }

}
