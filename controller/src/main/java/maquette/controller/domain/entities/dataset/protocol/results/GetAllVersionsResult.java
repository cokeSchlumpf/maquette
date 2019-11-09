package maquette.controller.domain.entities.dataset.protocol.results;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.dataset.VersionInfo;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetAllVersionsResult {

    private static final String VERSIONS = "versions";

    @JsonProperty(VERSIONS)
    private final Set<VersionInfo> versions;

    @JsonCreator
    public static GetAllVersionsResult apply(
        @JsonProperty(VERSIONS)Set<VersionInfo> versions) {

        return new GetAllVersionsResult(versions);
    }

}
