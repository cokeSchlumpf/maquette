package maquette.controller.domain.entities.notifcation.protocol.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.notifcation.protocol.Message;
import maquette.controller.domain.values.notification.Notification;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetNotificationResult implements Message {

    private static final String NOTIFICATION = "notification";

    @JsonProperty(NOTIFICATION)
    private Notification notification;

    @JsonCreator
    public static GetNotificationResult apply(
        @JsonProperty(NOTIFICATION) Notification notification) {

        return new GetNotificationResult(notification);
    }

}
