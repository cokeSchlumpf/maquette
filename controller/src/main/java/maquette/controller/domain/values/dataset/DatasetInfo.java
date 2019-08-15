package maquette.controller.domain.values.dataset;

import java.time.Instant;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasetInfo {

    private static final String DATASET = "dataset";
    private static final String MODIFIED = "modified";
    private static final String MODIFIED_BY = "modified-by";
    private static final String OWNER = "owner";
    private static final String VERSIONS = "versions";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(OWNER)
    private final UserId owner;

    @JsonProperty(MODIFIED)
    private final Instant modified;

    @JsonProperty(MODIFIED_BY)
    private final UserId modifiedBy;

    @JsonProperty(VERSIONS)
    private final Set<VersionInfo> versions;

    @JsonCreator
    public static DatasetInfo apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(OWNER) UserId owner,
        @JsonProperty(MODIFIED) Instant modified,
        @JsonProperty(MODIFIED_BY) UserId modifiedBy,
        @JsonProperty(VERSIONS) Set<VersionInfo> versions) {

        return new DatasetInfo(dataset, owner, modified, modifiedBy, versions);
    }

}
