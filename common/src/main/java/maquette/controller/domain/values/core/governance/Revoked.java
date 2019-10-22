package maquette.controller.domain.values.core.governance;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Revoked {

    private static final String BY = "by";
    private static final String AT = "at";
    private static final String COMMENT = "comment";

    @JsonProperty(BY)
    private final UserId by;

    @JsonProperty(AT)
    private final Instant at;

    @JsonProperty(COMMENT)
    private final String comment;

    @JsonCreator
    public static Revoked apply(
        @JsonProperty(BY) UserId by,
        @JsonProperty(AT) Instant at,
        @JsonProperty(COMMENT) String comment) {

        return new Revoked(by, at, comment);
    }

}
