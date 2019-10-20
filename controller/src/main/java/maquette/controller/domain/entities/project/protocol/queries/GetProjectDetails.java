package maquette.controller.domain.entities.project.protocol.queries;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.results.GetProjectDetailsResult;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourceName;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetProjectDetails implements ProjectMessage {

    public static final String ERROR_TO = "error-to";
    public static final String REPLY_TO = "reply-to";
    public static final String PROJECT = "project";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(REPLY_TO)
    private final ActorRef<GetProjectDetailsResult> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static GetProjectDetails apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(REPLY_TO) ActorRef<GetProjectDetailsResult> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new GetProjectDetails(project, replyTo, errorTo);
    }

}
