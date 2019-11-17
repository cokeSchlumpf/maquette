package maquette.controller.domain.entities.dataset.protocol.commands;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccessRequest;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RevokeDatasetAccessRequest implements DatasetMessage {

    private static final String ID = "id";
    private static final String DATASET = "dataset";
    private static final String EXECUTOR = "executor";
    private static final String COMMENT = "comment";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(ID)
    private final UID id;

    @JsonProperty(COMMENT)
    private final Markdown comment;

    @JsonProperty(REPLY_TO)
    private final ActorRef<RevokedDatasetAccessRequest> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static RevokeDatasetAccessRequest apply(
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(ID) UID id,
        @JsonProperty(COMMENT) Markdown comment,
        @JsonProperty(REPLY_TO) ActorRef<RevokedDatasetAccessRequest> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new RevokeDatasetAccessRequest(executor, dataset, id, comment, replyTo, errorTo);
    }

    public static RevokeDatasetAccessRequest apply(
        User executor, ResourcePath dataset, UID id,
        ActorRef<RevokedDatasetAccessRequest> replyTo, ActorRef<ErrorMessage> errorTo) {

        return apply(executor, dataset, id, null, replyTo, errorTo);
    }

    public Optional<Markdown> getComment() {
        return Optional.ofNullable(comment);
    }

}
