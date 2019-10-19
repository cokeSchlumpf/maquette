package maquette.controller.domain.entities.deprecatedproject.protocol.queries;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.deprecatedproject.protocol.ProjectsMessage;
import maquette.controller.domain.entities.deprecatedproject.protocol.results.ListProjectsResult;
import maquette.controller.domain.values.core.ErrorMessage;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ListProjects implements ProjectsMessage {

    public static final String ERROR_TO = "error-to";
    public static final String REPLY_TO = "reply-to";

    @JsonProperty(REPLY_TO)
    private final ActorRef<ListProjectsResult> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static ListProjects apply(
        @JsonProperty(REPLY_TO) ActorRef<ListProjectsResult> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new ListProjects(replyTo, errorTo);
    }

}
