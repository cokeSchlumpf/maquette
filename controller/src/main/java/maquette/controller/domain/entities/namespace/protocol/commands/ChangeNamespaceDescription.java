package maquette.controller.domain.entities.namespace.protocol.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.entities.namespace.protocol.events.ChangedNamespaceDescription;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeNamespaceDescription implements NamespaceMessage, NamespacesMessage {

    private static final String EXECUTOR = "executor";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(NAME)
    private final ResourceName name;

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(DESCRIPTION)
    private final Markdown description;

    @JsonProperty(REPLY_TO)
    private final ActorRef<ChangedNamespaceDescription> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static ChangeNamespaceDescription apply(
        @JsonProperty(NAME) ResourceName name,
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(DESCRIPTION) Markdown description,
        @JsonProperty(REPLY_TO) ActorRef<ChangedNamespaceDescription> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new ChangeNamespaceDescription(name, executor, description, replyTo, errorTo);
    }

}
