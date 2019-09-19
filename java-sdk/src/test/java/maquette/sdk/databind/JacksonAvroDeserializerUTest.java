package maquette.sdk.databind;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import maquette.sdk.TestType;
import maquette.sdk.util.Records;

public class JacksonAvroDeserializerUTest {

    @Test
    public void test() {
        List<TestType> foos = IntStream
            .range(0, 100)
            .mapToObj(i -> new TestType("foo", "bar" + i))
            .collect(Collectors.toList());

        JacksonAvroSerializer<TestType> serializer =
            JacksonAvroSerializer.apply(ObjectMapperFactory.apply().createAvroMapper(), TestType.class);

        JacksonAvroDeserializer<TestType> deserializer =
            JacksonAvroDeserializer.apply(ObjectMapperFactory.apply().createAvroMapper(), TestType.class);

        Records records = serializer.mapRecords(foos);
        Iterable<TestType> foos$decoded = deserializer.mapRecords(records);

        assertThat(foos$decoded).containsExactlyElementsOf(foos);
    }


}