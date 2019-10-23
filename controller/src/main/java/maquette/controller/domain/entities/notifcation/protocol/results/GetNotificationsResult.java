package maquette.controller.domain.entities.notifcation.protocol.results;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.notifcation.protocol.Message;
import maquette.controller.domain.values.notification.Notification;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetNotificationsResult implements Message {

    private static final String NOTIFICATIONS = "notifications";

    @JsonProperty(NOTIFICATIONS)
    private Set<Notification> notifications;

    @JsonCreator
    public static GetNotificationsResult apply(
        @JsonProperty(NOTIFICATIONS) Set<Notification> notifications) {

        return new GetNotificationsResult(ImmutableSet.copyOf(notifications));
    }

}
