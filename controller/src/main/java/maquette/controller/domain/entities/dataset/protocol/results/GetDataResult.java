package maquette.controller.domain.entities.dataset.protocol.results;

import java.util.List;

import org.apache.avro.generic.GenericData;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetDataResult {

    private final List<GenericData.Record> records;

    @JsonCreator
    public static GetDataResult apply(
        @JsonProperty("records") List<GenericData.Record> records) {

        return new GetDataResult(ImmutableList.copyOf(records));
    }

}
