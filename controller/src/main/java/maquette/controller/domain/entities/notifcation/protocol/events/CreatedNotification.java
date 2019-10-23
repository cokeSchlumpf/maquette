package maquette.controller.domain.entities.notifcation.protocol.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.notifcation.protocol.NotificationsEvent;
import maquette.controller.domain.values.notification.Notification;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatedNotification implements NotificationsEvent {

    private static final String CREATED_AT = "created-at";
    private static final String NOTIFICATION = "notification";

    @JsonProperty(CREATED_AT)
    private final Instant createdAt;

    @JsonProperty(NOTIFICATION)
    private final Notification notification;

    @JsonCreator
    public static CreatedNotification apply(
        @JsonProperty(CREATED_AT) Instant createdAt,
        @JsonProperty(NOTIFICATION) Notification notification) {

        return new CreatedNotification(createdAt, notification);
    }

}
