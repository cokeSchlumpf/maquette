package maquette.controller.domain.entities.dataset.protocol.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.VersionMessage;
import maquette.controller.domain.entities.dataset.protocol.events.CommittedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.PublishedDatasetVersion;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommitDatasetVersion implements VersionMessage {

    private static final String DATASET = "dataset";
    private static final String EXECUTOR = "executor";
    private static final String MESSAGE = "message";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";
    private static final String VERSION_ID = "version-id";

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(VERSION_ID)
    private final UID versionId;

    @JsonProperty(MESSAGE)
    private final String message;

    @JsonProperty(REPLY_TO)
    private final ActorRef<CommittedDatasetVersion> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static CommitDatasetVersion apply(
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(VERSION_ID) UID versionId,
        @JsonProperty(MESSAGE) String message,
        @JsonProperty(REPLY_TO) ActorRef<CommittedDatasetVersion> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new CommitDatasetVersion(executor, dataset, versionId, message, replyTo, errorTo);
    }

}
