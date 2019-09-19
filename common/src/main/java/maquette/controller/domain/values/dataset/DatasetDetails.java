package maquette.controller.domain.values.dataset;

import java.time.Instant;
import java.util.Comparator;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;
import maquette.controller.domain.values.exceptions.NoVersionException;
import maquette.controller.domain.values.exceptions.UnknownVersionException;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.UserId;

@Value
@Wither
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasetDetails {

    private static final String ACL = "acl";
    private static final String CREATED = "created";
    private static final String CREATED_BY = "created-by";
    private static final String DATASET = "dataset";
    private static final String MODIFIED = "modified";
    private static final String MODIFIED_BY = "modified-by";
    private static final String VERSIONS = "versions";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(CREATED)
    private final Instant created;

    @JsonProperty(CREATED_BY)
    private final UserId createdBy;

    @JsonProperty(MODIFIED)
    private final Instant modified;

    @JsonProperty(MODIFIED_BY)
    private final UserId modifiedBy;

    @JsonProperty(VERSIONS)
    private final Set<VersionInfo> versions;

    @JsonProperty(ACL)
    private final DatasetACL acl;

    @JsonCreator
    public static DatasetDetails apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(CREATED) Instant created,
        @JsonProperty(CREATED_BY) UserId createdBy,
        @JsonProperty(MODIFIED) Instant modified,
        @JsonProperty(MODIFIED_BY) UserId modifiedBy,
        @JsonProperty(VERSIONS) Set<VersionInfo> versions,
        @JsonProperty(ACL) DatasetACL acl) {

        return new DatasetDetails(dataset, created, createdBy, modified, modifiedBy, versions, acl);
    }

    public UID findVersionId(VersionTag tag) {
        return versions
            .stream()
            .filter(info -> {
                VersionTag itag = info.getVersion().orElse(null);
                return itag != null && itag.equals(tag);
            })
            .map(VersionInfo::getVersionId)
            .findAny()
            .orElseThrow(() -> UnknownVersionException.apply(tag));
    }

    public UID findLatestVersion() {
        return versions
            .stream()
            .filter(info -> info.getVersion().isPresent())
            .max(Comparator.comparing(info -> info.getVersion().get()))
            .map(VersionInfo::getVersionId)
            .orElseThrow(NoVersionException::apply);
    }

}
