package maquette.controller.domain.entities.project.protocol.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectPrivacy;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeProjectPrivacy implements ProjectMessage {

    private static final String EXECUTOR = "executor";
    private static final String PROJECT = "project";
    private static final String IS_PRIVATE = "private";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(IS_PRIVATE)
    private final boolean isPrivate;

    @JsonProperty(REPLY_TO)
    private final ActorRef<ChangedProjectPrivacy> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static ChangeProjectPrivacy apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(IS_PRIVATE) boolean isPrivate,
        @JsonProperty(REPLY_TO) ActorRef<ChangedProjectPrivacy> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new ChangeProjectPrivacy(project, executor, isPrivate, replyTo, errorTo);
    }

    @Deprecated
    public static ChangeProjectPrivacy apply(
        ResourceName project, User executor, ActorRef<ChangedProjectPrivacy> replyTo, ActorRef<ErrorMessage> errorTo) {
        return apply(project, executor, false, replyTo, errorTo);
    }

}
