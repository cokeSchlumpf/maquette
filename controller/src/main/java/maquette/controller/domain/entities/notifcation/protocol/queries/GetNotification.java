package maquette.controller.domain.entities.notifcation.protocol.queries;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.notifcation.protocol.NotificationsMessage;
import maquette.controller.domain.entities.notifcation.protocol.results.GetNotificationResult;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetNotification implements NotificationsMessage {

    private static final String EXECUTOR = "executor";
    private static final String ID = "id";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(ID)
    private final UID id;

    @JsonProperty(REPLY_TO)
    private final ActorRef<GetNotificationResult> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static GetNotification apply(
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(ID) UID id,
        @JsonProperty(REPLY_TO) ActorRef<GetNotificationResult> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new GetNotification(executor, id, replyTo, errorTo);
    }

}
