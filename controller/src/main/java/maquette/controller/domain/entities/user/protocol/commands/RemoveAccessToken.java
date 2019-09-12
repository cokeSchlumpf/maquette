package maquette.controller.domain.entities.user.protocol.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.entities.user.protocol.events.RemovedAccessToken;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RemoveAccessToken implements UserMessage {

    private static final String EXECUTOR = "executor";
    private static final String NAME = "name";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(NAME)
    private final ResourceName name;

    @JsonProperty(REPLY_TO)
    private final ActorRef<RemovedAccessToken> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static RemoveAccessToken apply(
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(NAME) ResourceName name,
        @JsonProperty(REPLY_TO) ActorRef<RemovedAccessToken> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new RemoveAccessToken(executor, name, replyTo, errorTo);
    }

}
