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
import maquette.controller.domain.entities.dataset.protocol.events.GrantedDatasetAccess;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.ErrorMessage;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GrantDatasetAccess implements DatasetMessage {

    private static final String EXECUTOR = "executor";
    private static final String GRANT = "grant";
    private static final String GRANT_FOR = "grant-for";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(GRANT)
    private final Set<DatasetPrivilege> grant;

    @JsonProperty(GRANT_FOR)
    private final Authorization grantFor;

    @JsonProperty(REPLY_TO)
    private final ActorRef<GrantedDatasetAccess> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static GrantDatasetAccess apply(
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(GRANT) Set<DatasetPrivilege> grant,
        @JsonProperty(GRANT_FOR) Authorization grantFor,
        @JsonProperty(REPLY_TO) ActorRef<GrantedDatasetAccess> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new GrantDatasetAccess(executor, ImmutableSet.copyOf(grant), grantFor, replyTo, errorTo);
    }

}
