package maquette.controller.domain.entities.dataset.protocol.commands;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccess;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.ErrorMessage;
import maquette.controller.domain.values.iam.User;
import netscape.security.Privilege;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RevokeDatasetAccess implements DatasetMessage {

    private static final String EXECUTOR = "executor";
    private static final String REVOKE = "revoke";
    private static final String REVOKE_FROM = "revoke-from";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(REVOKE)
    private final Set<Privilege> revoke;

    @JsonProperty(REVOKE_FROM)
    private final Authorization revokeFrom;

    @JsonProperty(REPLY_TO)
    private final ActorRef<RevokedDatasetAccess> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static RevokeDatasetAccess apply(
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(REVOKE) Set<Privilege> revoke,
        @JsonProperty(REVOKE_FROM) Authorization revokeFrom,
        @JsonProperty(REPLY_TO) ActorRef<RevokedDatasetAccess> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new RevokeDatasetAccess(executor, ImmutableSet.copyOf(revoke), revokeFrom, replyTo, errorTo);
    }

}
