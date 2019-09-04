package maquette.controller.domain.values.dataset;

import java.time.Instant;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.UserId;

@Value
@Wither
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VersionDetails {

    private static final String CREATED = "created";
    private static final String CREATED_BY = "created-by";
    private static final String LAST_MODIFIED = "last-modified";
    private static final String MODIFIED_BY = "modified-by";
    private static final String COMMIT = "commit";
    private static final String RECORDS = "records";
    private static final String VERSION_ID = "version-id";

    @JsonProperty(VERSION_ID)
    private final UID versionId;

    @JsonProperty(CREATED)
    private final Instant created;

    @JsonProperty(CREATED_BY)
    private final UserId createdBy;

    @JsonProperty(LAST_MODIFIED)
    private final Instant lastModified;

    @JsonProperty(MODIFIED_BY)
    private final UserId modifiedBy;

    @JsonProperty(RECORDS)
    private final long records;

    @JsonProperty(COMMIT)
    private final Commit commit;

    @JsonCreator
    public static VersionDetails apply(
        @JsonProperty(VERSION_ID) UID versionId,
        @JsonProperty(CREATED) Instant created,
        @JsonProperty(CREATED_BY) UserId createdBy,
        @JsonProperty(LAST_MODIFIED) Instant lastModified,
        @JsonProperty(MODIFIED_BY) UserId modifiedBy,
        @JsonProperty(RECORDS) long records,
        @JsonProperty(COMMIT) Commit commit) {

        return new VersionDetails(versionId, created, createdBy, lastModified, modifiedBy, records, commit);
    }

    public static VersionDetails apply(
        UID versionId, Instant created, UserId createdBy, Instant lastModified, UserId modifiedBy, long records) {

        return apply(versionId, created, createdBy, lastModified, modifiedBy, records, null);
    }

    public Optional<Commit> getCommit() {
        return Optional.ofNullable(commit);
    }

}
