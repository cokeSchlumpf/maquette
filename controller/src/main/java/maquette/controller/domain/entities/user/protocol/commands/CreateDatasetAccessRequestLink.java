package maquette.controller.domain.entities.user.protocol.commands;

import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.entities.user.protocol.events.CreatedDatasetAccessRequestLink;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.dataset.DatasetAccessRequestLink;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateDatasetAccessRequestLink implements UserMessage {

    private static final String REQUEST = "request";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(REQUEST)
    private final DatasetAccessRequestLink request;

    @JsonProperty(REPLY_TO)
    private final ActorRef<CreatedDatasetAccessRequestLink> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    public static CreateDatasetAccessRequestLink apply(
        @JsonProperty(REQUEST) DatasetAccessRequestLink request,
        @JsonProperty(REPLY_TO) ActorRef<CreatedDatasetAccessRequestLink> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new CreateDatasetAccessRequestLink(request, replyTo, errorTo);
    }

}
