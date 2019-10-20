package maquette.controller.domain.entities.project.protocol.commands;

import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.events.RevokedProjectAccess;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectPrivilege;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RevokeProjectAccess implements ProjectMessage {

    private static final String EXECUTOR = "executor";
    private static final String REVOKE = "revoke";
    private static final String REVOKE_FROM = "revoke-from";
    private static final String PROJECT = "project";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(REVOKE)
    private final ProjectPrivilege revoke;

    @JsonProperty(REVOKE_FROM)
    private final Authorization revokeFrom;

    @JsonProperty(REPLY_TO)
    private final ActorRef<RevokedProjectAccess> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    public static RevokeProjectAccess apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(REVOKE) ProjectPrivilege revoke,
        @JsonProperty(REVOKE_FROM) Authorization revokeFrom,
        @JsonProperty(REPLY_TO) ActorRef<RevokedProjectAccess> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new RevokeProjectAccess(project, executor, revoke, revokeFrom, replyTo, errorTo);
    }


}
