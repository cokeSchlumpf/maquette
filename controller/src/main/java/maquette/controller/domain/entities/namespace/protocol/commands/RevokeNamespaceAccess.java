package maquette.controller.domain.entities.namespace.protocol.commands;

import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.events.RevokedNamespaceAccess;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespacePrivilege;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RevokeNamespaceAccess implements NamespaceMessage {

    private static final String EXECUTOR = "executor";
    private static final String REVOKE = "revoke";
    private static final String REVOKE_FROM = "revoke-from";
    private static final String NAME = "name";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(NAME)
    private final ResourceName name;

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(REVOKE)
    private final NamespacePrivilege revoke;

    @JsonProperty(REVOKE_FROM)
    private final Authorization revokeFrom;

    @JsonProperty(REPLY_TO)
    private final ActorRef<RevokedNamespaceAccess> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    public static RevokeNamespaceAccess apply(
        @JsonProperty(NAME) ResourceName name,
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(REVOKE) NamespacePrivilege revoke,
        @JsonProperty(REVOKE_FROM) Authorization revokeFrom,
        @JsonProperty(REPLY_TO) ActorRef<RevokedNamespaceAccess> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new RevokeNamespaceAccess(name, executor, revoke, revokeFrom, replyTo, errorTo);
    }


}
