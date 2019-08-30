package maquette.controller.domain.entities.dataset.protocol.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.dataset.VersionDetails;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetVersionDetailsResult {

    private final VersionDetails details;

    @JsonCreator
    public static GetVersionDetailsResult apply(
        @JsonProperty("details") VersionDetails details) {

        return new GetVersionDetailsResult(details);
    }

}
