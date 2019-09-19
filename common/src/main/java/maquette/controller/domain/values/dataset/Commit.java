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
public class Commit {

    private static final String COMMITTED_BY = "committed-by";
    private static final String COMMITTED_AT = "committed-at";
    private static final String MESSAGE = "message";

    @JsonProperty(COMMITTED_BY)
    private final UserId committedBy;

    @JsonProperty(COMMITTED_AT)
    private final Instant committedAt;

    @JsonProperty(MESSAGE)
    private final String message;

    @JsonCreator
    public static Commit apply(
        @JsonProperty(COMMITTED_BY) UserId committedBy,
        @JsonProperty(COMMITTED_AT) Instant committedAt,
        @JsonProperty(MESSAGE) String message) {

        return new Commit(committedBy, committedAt, message);
    }

}
