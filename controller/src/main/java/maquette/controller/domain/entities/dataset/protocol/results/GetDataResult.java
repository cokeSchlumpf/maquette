package maquette.controller.domain.entities.dataset.protocol.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.Message;
import maquette.controller.domain.values.core.records.Records;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetDataResult implements Message {

    private final Records records;

    @JsonCreator
    public static GetDataResult apply(
        @JsonProperty("records") Records records) {

        return new GetDataResult(records);
    }

}
