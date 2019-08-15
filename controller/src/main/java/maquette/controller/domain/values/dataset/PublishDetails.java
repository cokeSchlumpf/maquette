package maquette.controller.domain.values.dataset;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PublishDetails {

    private static final String VERSION = "version";
    private static final String PUBLISHED_BY = "published-by";
    private static final String PUBLISHED_AT = "published-at";
    private static final String MESSAGE = "message";

    @JsonProperty(VERSION)
    private final Version version;

    @JsonProperty(PUBLISHED_BY)
    private final UserId publishedBy;

    @JsonProperty(PUBLISHED_AT)
    private final Instant publishedAt;

    @JsonProperty(MESSAGE)
    private final String message;

    @JsonCreator
    public static PublishDetails apply(
        @JsonProperty(VERSION) Version version,
        @JsonProperty(PUBLISHED_BY) UserId publishedBy,
        @JsonProperty(PUBLISHED_AT) Instant publishedAt,
        @JsonProperty(MESSAGE) String message) {

        return new PublishDetails(version, publishedBy, publishedAt, message);
    }

}
