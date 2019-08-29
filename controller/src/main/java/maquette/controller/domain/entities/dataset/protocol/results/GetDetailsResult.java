package maquette.controller.domain.entities.dataset.protocol.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.dataset.DatasetDetails;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetDetailsResult {

    private final DatasetDetails details;

    @JsonCreator
    public static GetDetailsResult apply(
        @JsonProperty("details") DatasetDetails details) {

        return new GetDetailsResult(details);
    }

}
