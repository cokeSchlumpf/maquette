package maquette.controller.domain.entities.namespace.protocol.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.entities.namespace.protocol.events.CreatedNamespace;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateNamespace implements NamespaceMessage, NamespacesMessage {

    private static final String EXECUTOR = "executor";
    private static final String NAME = "name";
    private static final String IS_PRIVATE = "is-private";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(NAME)
    private final ResourceName name;

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(IS_PRIVATE)
    private final boolean isPrivate;

    @JsonProperty(REPLY_TO)
    private final ActorRef<CreatedNamespace> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static CreateNamespace apply(
        @JsonProperty(NAME) ResourceName name,
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(IS_PRIVATE) boolean isPrivate,
        @JsonProperty(REPLY_TO) ActorRef<CreatedNamespace> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new CreateNamespace(name, executor, isPrivate, replyTo, errorTo);
    }

    @Deprecated
    public static CreateNamespace apply(
        ResourceName name, User executor, ActorRef<CreatedNamespace> replyTo, ActorRef<ErrorMessage> errorTo) {

        return new CreateNamespace(name, executor, false, replyTo, errorTo);
    }

}
