package maquette.controller.domain.entities.project.protocol.commands;

import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.events.GrantedProjectAccess;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.NamespacePrivilege;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GrantProjectAccess implements ProjectMessage {

    private static final String EXECUTOR = "executor";
    private static final String GRANT = "grant";
    private static final String GRANT_FOR = "grant-for";
    private static final String PROJECT = "project";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(GRANT)
    private final NamespacePrivilege grant;

    @JsonProperty(GRANT_FOR)
    private final Authorization grantFor;

    @JsonProperty(REPLY_TO)
    private final ActorRef<GrantedProjectAccess> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    public static GrantProjectAccess apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(GRANT) NamespacePrivilege grant,
        @JsonProperty(GRANT_FOR) Authorization grantFor,
        @JsonProperty(REPLY_TO) ActorRef<GrantedProjectAccess> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new GrantProjectAccess(project, executor, grant, grantFor, replyTo, errorTo);
    }


}
