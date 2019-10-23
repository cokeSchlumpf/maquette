package maquette.controller.domain.entities.notifcation.protocol.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.notifcation.protocol.NotificationsMessage;
import maquette.controller.domain.entities.notifcation.protocol.events.MarkedNotificationAsRead;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MarkNotificationAsRead implements NotificationsMessage {

    private static final String EXECUTOR = "executor";
    private static final String NOTIFICATION = "notification";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(NOTIFICATION)
    private final UID notification;

    @JsonProperty(REPLY_TO)
    private final ActorRef<MarkedNotificationAsRead> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static MarkNotificationAsRead apply(
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(NOTIFICATION) UID notification,
        @JsonProperty(REPLY_TO) ActorRef<MarkedNotificationAsRead> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new MarkNotificationAsRead(executor, notification, replyTo, errorTo);
    }

}
