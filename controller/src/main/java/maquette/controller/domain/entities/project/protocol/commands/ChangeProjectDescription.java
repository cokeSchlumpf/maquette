package maquette.controller.domain.entities.project.protocol.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectDescription;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeProjectDescription implements ProjectMessage {

    private static final String EXECUTOR = "executor";
    private static final String PROJECT = "project";
    private static final String DESCRIPTION = "description";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(DESCRIPTION)
    private final Markdown description;

    @JsonProperty(REPLY_TO)
    private final ActorRef<ChangedProjectDescription> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static ChangeProjectDescription apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(DESCRIPTION) Markdown description,
        @JsonProperty(REPLY_TO) ActorRef<ChangedProjectDescription> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new ChangeProjectDescription(project, executor, description, replyTo, errorTo);
    }

}
