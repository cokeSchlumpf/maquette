package maquette.controller.domain.entities.project.protocol.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.ProjectsMessage;
import maquette.controller.domain.entities.project.protocol.events.DeletedProject;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteProject implements ProjectMessage, ProjectsMessage {

    private static final String EXECUTOR = "executor";
    private static final String PROJECT = "project";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(REPLY_TO)
    private final ActorRef<DeletedProject> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static DeleteProject apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(REPLY_TO) ActorRef<DeletedProject> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new DeleteProject(project, executor, replyTo, errorTo);
    }

}
