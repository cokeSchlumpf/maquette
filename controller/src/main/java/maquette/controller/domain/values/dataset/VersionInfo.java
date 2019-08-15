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
public class VersionInfo {

    private static final String CREATED = "created";
    private static final String CREATED_BY = "created-by";
    private static final String LAST_MODIFIED = "last-modified";
    private static final String MODIFIED_BY = "modified-by";
    private static final String PUBLISHED = "published";
    private static final String RECORDS = "records";
    private static final String VERSION_ID = "version-id";
    private static final String VERSION = "version";

    @JsonProperty(VERSION_ID)
    private final UID versionId;

    @JsonProperty(LAST_MODIFIED)
    private final Instant lastModified;

    @JsonProperty(MODIFIED_BY)
    private final UserId modifiedBy;

    @JsonProperty(RECORDS)
    private final long records;

    @JsonProperty(VERSION)
    private final Version version;

    @JsonCreator
    public static VersionInfo apply(
        @JsonProperty(VERSION_ID) UID versionId,
        @JsonProperty(LAST_MODIFIED) Instant lastModified,
        @JsonProperty(MODIFIED_BY) UserId modifiedBy,
        @JsonProperty(RECORDS) long records,
        @JsonProperty(VERSION) Version version) {

        return new VersionInfo(versionId, lastModified, modifiedBy, records, version);
    }

    public static VersionInfo apply(
        UID versionId, Instant lastModified, UserId modifiedBy, long records) {

        return apply(versionId, lastModified, modifiedBy, records, null);
    }

    public Optional<Version> getVersion() {
        return Optional.ofNullable(version);
    }

}
