package maquette.sdk.databind;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import maquette.controller.domain.values.core.records.Records;
import maquette.sdk.TestType;

public class ReflectiveSerializationUTest {

    @Test
    public void test() {
        List<TestType> foos = IntStream
            .range(0, 100)
            .mapToObj(i -> new TestType("foo", "bar" + i))
            .collect(Collectors.toList());

        ReflectiveAvroSerializer<TestType> serializer =
            ReflectiveAvroSerializer.apply(TestType.class);

        ReflectiveAvroDeserializer<TestType> deserializer =
            ReflectiveAvroDeserializer.apply(TestType.class);

        Records records = serializer.mapRecords(foos);
        Iterable<TestType> foos$decoded = deserializer.mapRecords(records);

        assertThat(foos$decoded).containsExactlyElementsOf(foos);
    }


}