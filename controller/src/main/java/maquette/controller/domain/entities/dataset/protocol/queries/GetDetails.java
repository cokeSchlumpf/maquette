package maquette.controller.domain.entities.dataset.protocol.queries;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.results.GetDetailsResult;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourcePath;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetDetails implements DatasetMessage {

    private static final String DATASET = "dataset";
    private static final String ERROR_TO = "error-to";
    private static final String REPLY_TO = "reply-to";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(REPLY_TO)
    private final ActorRef<GetDetailsResult> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static GetDetails apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(REPLY_TO) ActorRef<GetDetailsResult> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new GetDetails(dataset, replyTo, errorTo);
    }

}
