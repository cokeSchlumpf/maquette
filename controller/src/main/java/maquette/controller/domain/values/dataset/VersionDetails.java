package maquette.controller.domain.values.dataset;

import java.time.Instant;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;
import maquette.controller.domain.values.iam.UserId;

@Value
@Wither
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VersionDetails {

    private static final String CREATED = "created";
    private static final String CREATED_BY = "created-by";
    private static final String LAST_MODIFIED = "last-modified";
    private static final String MODIFIED_BY = "modified-by";
    private static final String PUBLISHED = "published";
    private static final String RECORDS = "records";

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

    @JsonProperty(PUBLISHED)
    private final Published published;

    @JsonCreator
    public static VersionDetails apply(
        @JsonProperty(CREATED) Instant created,
        @JsonProperty(CREATED_BY) UserId createdBy,
        @JsonProperty(LAST_MODIFIED) Instant lastModified,
        @JsonProperty(MODIFIED_BY) UserId modifiedBy,
        @JsonProperty(RECORDS) long records,
        @JsonProperty(PUBLISHED) Published published) {

        return new VersionDetails(created, createdBy, lastModified, modifiedBy, records, published);
    }

    public static VersionDetails apply(
        Instant created, UserId createdBy, Instant lastModified, UserId modifiedBy, long records) {

        return apply(created, createdBy, lastModified, modifiedBy, records, null);
    }

    public Optional<Published> getPublished() {
        return Optional.ofNullable(published);
    }

}
