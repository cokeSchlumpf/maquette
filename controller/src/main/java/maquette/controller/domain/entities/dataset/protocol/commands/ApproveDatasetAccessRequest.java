package maquette.controller.domain.entities.dataset.protocol.commands;

import org.checkerframework.checker.guieffect.qual.UI;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.events.ApprovedDatasetAccessRequest;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApproveDatasetAccessRequest implements DatasetMessage {

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
    private final String comment;

    @JsonProperty(REPLY_TO)
    private final ActorRef<ApprovedDatasetAccessRequest> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static ApproveDatasetAccessRequest apply(
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(ID) UID id,
        @JsonProperty(COMMENT) String comment,
        @JsonProperty(REPLY_TO) ActorRef<ApprovedDatasetAccessRequest> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new ApproveDatasetAccessRequest(executor, dataset, id, comment, replyTo, errorTo);
    }

}
