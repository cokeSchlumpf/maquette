package maquette.controller.domain.entities.dataset.protocol.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedOwner;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeOwner implements DatasetMessage {

    private static final String EXECUTOR = "executor";
    private static final String DATASET = "dataset";
    private static final String OWNER = "owner";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(OWNER)
    private final Authorization owner;

    @JsonProperty(REPLY_TO)
    private final ActorRef<ChangedOwner> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static ChangeOwner apply(
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(OWNER) Authorization owner,
        @JsonProperty(REPLY_TO) ActorRef<ChangedOwner> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new ChangeOwner(dataset, executor, owner, replyTo, errorTo);
    }

}
