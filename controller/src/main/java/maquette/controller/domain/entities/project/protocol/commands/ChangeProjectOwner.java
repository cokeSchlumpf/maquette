package maquette.controller.domain.entities.project.protocol.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectOwner;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeProjectOwner implements ProjectMessage {

    private static final String EXECUTOR = "executor";
    private static final String PROJECT = "project";
    private static final String OWNER = "owner";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(OWNER)
    private final Authorization owner;

    @JsonProperty(REPLY_TO)
    private final ActorRef<ChangedProjectOwner> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static ChangeProjectOwner apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(OWNER) Authorization owner,
        @JsonProperty(REPLY_TO) ActorRef<ChangedProjectOwner> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new ChangeProjectOwner(project, executor, owner, replyTo, errorTo);
    }

}
