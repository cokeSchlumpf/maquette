package maquette.controller.domain.entities.namespace.protocol.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.events.RegisteredDataset;
import maquette.controller.domain.entities.namespace.protocol.events.RemovedDataset;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.ErrorMessage;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RemoveDataset implements NamespaceMessage {

    private static final String DATASET = "dataset";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(DATASET)
    private final ResourceName dataset;

    @JsonProperty(REPLY_TO)
    private final ActorRef<RemovedDataset> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static RemoveDataset apply(
        @JsonProperty(DATASET) ResourceName dataset,
        @JsonProperty(REPLY_TO) ActorRef<RemovedDataset> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new RemoveDataset(dataset, replyTo, errorTo);
    }

}
