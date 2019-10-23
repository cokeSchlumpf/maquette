package maquette.controller.domain.entities.notifcation.protocol.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.notifcation.protocol.NotificationsEvent;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MarkedNotificationAsRead implements NotificationsEvent {

    private static final String READ_AT = "read-at";
    private static final String READ_BY = "read-by";
    private static final String NOTIFICATION = "notification";

    @JsonProperty(READ_BY)
    private final UserId readBy;

    @JsonProperty(READ_AT)
    private final Instant readAt;

    @JsonProperty(NOTIFICATION)
    private final UID notification;

    @JsonCreator
    public static MarkedNotificationAsRead apply(
        @JsonProperty(READ_BY) UserId readBy,
        @JsonProperty(READ_AT) Instant readAt,
        @JsonProperty(NOTIFICATION) UID notification) {

        return new MarkedNotificationAsRead(readBy, readAt, notification);
    }

}
