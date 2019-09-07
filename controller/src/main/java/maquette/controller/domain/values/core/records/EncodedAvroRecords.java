package maquette.controller.domain.values.core.records;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.util.ByteBufferInputStream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import akka.util.ByteString;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.domain.util.Operators;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
final class EncodedAvroRecords implements Records {

    private final List<ByteString> data;

    public InputStream asInputStream() {
        List<ByteBuffer> buffers = data.stream().map(ByteString::asByteBuffer).collect(Collectors.toList());
        return new ByteBufferInputStream(buffers);
    }

    @Override
    public Schema getSchema() {
        DataFileStream<GenericRecord> dataFileStream = getDataFileStream();
        return dataFileStream.getSchema();
    }

    @Override
    public List<GenericData.Record> getRecords() {
        DataFileStream<GenericRecord> dataFileStream = getDataFileStream();

        return Lists
            .newArrayList(dataFileStream.iterator())
            .stream()
            .map(record -> (GenericData.Record) record)
            .collect(Collectors.toList());
    }

    @Override
    public List<ByteString> getBytes() {
        return data;
    }

    @Override
    public int size() {
        return getRecords().size();
    }

    private DataFileStream<GenericRecord> getDataFileStream() {
        return Operators.suppressExceptions(() -> {
            List<ByteBuffer> buffers = data.stream().map(ByteString::asByteBuffer).collect(Collectors.toList());
            DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
            return new DataFileStream<>(new ByteBufferInputStream(buffers), datumReader);
        });
    }

}
