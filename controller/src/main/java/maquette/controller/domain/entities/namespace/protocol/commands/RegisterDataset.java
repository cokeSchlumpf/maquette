package maquette.controller.domain.entities.namespace.protocol.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.events.RegisteredDataset;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ErrorMessage;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisterDataset implements NamespaceMessage {

    private static final String DATASET = "dataset";
    private static final String NAME = "name";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(NAME)
    private final ResourceName namespace;

    @JsonProperty(DATASET)
    private final ResourceName dataset;

    @JsonProperty(REPLY_TO)
    private final ActorRef<RegisteredDataset> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static RegisterDataset apply(
        @JsonProperty(NAME) ResourceName namespace,
        @JsonProperty(DATASET) ResourceName dataset,
        @JsonProperty(REPLY_TO) ActorRef<RegisteredDataset> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new RegisterDataset(namespace, dataset, replyTo, errorTo);
    }

}
