package maquette.controller.domain.entities.notifcation.protocol.commands;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.notifcation.protocol.NotificationsMessage;
import maquette.controller.domain.entities.notifcation.protocol.events.CreatedNotification;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.notification.actions.NotificationAction;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateNotification implements NotificationsMessage {

    private static final String SENT = "sent";
    private static final String TO = "to";
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String ACTIONS = "actions";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(TO)
    private final Authorization to;

    @JsonProperty(MESSAGE)
    private final Markdown message;

    @JsonProperty(ACTIONS)
    private final List<NotificationAction> actions;

    @JsonProperty(REPLY_TO)
    private final ActorRef<CreatedNotification> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static CreateNotification apply(
        @JsonProperty(TO) Authorization to,
        @JsonProperty(MESSAGE) Markdown message,
        @JsonProperty(ACTIONS) List<NotificationAction> actions,
        @JsonProperty(REPLY_TO) ActorRef<CreatedNotification> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new CreateNotification(to, message, ImmutableList.copyOf(actions), replyTo, errorTo);
    }

    public static CreateNotification apply(Authorization to, Markdown message,
                                           ActorRef<CreatedNotification> replyTo, ActorRef<ErrorMessage> errorTo) {

        return apply(to, message, Lists.newArrayList(), replyTo, errorTo);
    }

}
