package maquette.controller.domain.entities.user.protocol.commands;

import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.entities.user.protocol.events.ConfiguredNamespace;
import maquette.controller.domain.entities.user.protocol.events.RegisteredAccessToken;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigureNamespace implements UserMessage {

    private static final String EXECUTOR = "executor";
    private static final String NAMESPACE = "namespace";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(NAMESPACE)
    private final ResourceName namespace;

    @JsonProperty(REPLY_TO)
    private final ActorRef<ConfiguredNamespace> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    public static ConfigureNamespace apply(
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(NAMESPACE) ResourceName namespace,
        @JsonProperty(REPLY_TO) ActorRef<ConfiguredNamespace> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new ConfigureNamespace(executor, namespace, replyTo, errorTo);
    }

}
