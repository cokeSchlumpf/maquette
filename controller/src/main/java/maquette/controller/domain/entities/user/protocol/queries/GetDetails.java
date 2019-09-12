package maquette.controller.domain.entities.user.protocol.queries;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.entities.user.protocol.results.GetDetailsResult;
import maquette.controller.domain.values.core.ErrorMessage;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetDetails implements UserMessage {

    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(REPLY_TO)
    private final ActorRef<GetDetailsResult> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static GetDetails apply(
        @JsonProperty(REPLY_TO) ActorRef<GetDetailsResult> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new GetDetails(replyTo, errorTo);
    }

}
