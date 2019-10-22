package maquette.controller.domain.entities.dataset.protocol.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDataset;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.governance.GovernanceProperties;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateDataset implements DatasetMessage {

    private static final String DATASET = "dataset";
    private static final String DESCRIPTION = "description";
    private static final String EXECUTOR = "executor";
    private static final String GOVERNANCE = "governance";
    private static final String IS_PRIVATE = "private";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(DESCRIPTION)
    private final Markdown description;

    @JsonProperty(IS_PRIVATE)
    private final boolean isPrivate;

    @JsonProperty(GOVERNANCE)
    private final GovernanceProperties governance;

    @JsonProperty(REPLY_TO)
    private final ActorRef<CreatedDataset> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static CreateDataset apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(DESCRIPTION) Markdown description,
        @JsonProperty(IS_PRIVATE) boolean isPrivate,
        @JsonProperty(GOVERNANCE) GovernanceProperties governance,
        @JsonProperty(REPLY_TO) ActorRef<CreatedDataset> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new CreateDataset(dataset, executor, description, isPrivate, governance, replyTo, errorTo);
    }

}
