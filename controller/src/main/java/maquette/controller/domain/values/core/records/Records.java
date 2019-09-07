package maquette.controller.domain.values.core.records;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import akka.util.ByteString;

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

    Schema getSchema();

    List<GenericData.Record> getRecords();

    List<ByteString> getBytes();

    int size();

}
