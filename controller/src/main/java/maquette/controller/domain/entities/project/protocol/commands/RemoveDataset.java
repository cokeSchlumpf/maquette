package maquette.controller.domain.entities.project.protocol.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.events.RemovedDataset;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ErrorMessage;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RemoveDataset implements ProjectMessage {

    private static final String DATASET = "dataset";
    private static final String PROJECT = "project";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(DATASET)
    private final ResourceName dataset;

    @JsonProperty(REPLY_TO)
    private final ActorRef<RemovedDataset> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static RemoveDataset apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(DATASET) ResourceName dataset,
        @JsonProperty(REPLY_TO) ActorRef<RemovedDataset> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new RemoveDataset(project, dataset, replyTo, errorTo);
    }

}
