package maquette.controller.domain.values.notification;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationRead {

    private static final String BY = "by";
    private static final String AT = "at";

    @JsonProperty(BY)
    private final UserId by;

    @JsonProperty(AT)
    private final Instant at;

    @JsonCreator
    public static NotificationRead apply(
        @JsonProperty(BY) UserId by,
        @JsonProperty(AT) Instant at) {

        return new NotificationRead(by, at);
    }

}
