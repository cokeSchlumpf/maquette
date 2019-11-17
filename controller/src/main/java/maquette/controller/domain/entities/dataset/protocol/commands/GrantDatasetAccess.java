package maquette.controller.domain.entities.dataset.protocol.commands;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.events.GrantedDatasetAccess;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.User;
import sun.jvm.hotspot.oops.Mark;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GrantDatasetAccess implements DatasetMessage {

    private static final String DATASET = "dataset";
    private static final String EXECUTOR = "executor";
    private static final String GRANT = "grant";
    private static final String GRANT_FOR = "grant-for";
    private static final String JUSTIFICATION = "justification";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(GRANT)
    private final DatasetPrivilege grant;

    @JsonProperty(GRANT_FOR)
    private final Authorization grantFor;

    @JsonProperty(JUSTIFICATION)
    private final Markdown justification;

    @JsonProperty(REPLY_TO)
    private final ActorRef<GrantedDatasetAccess> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static GrantDatasetAccess apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(GRANT) DatasetPrivilege grant,
        @JsonProperty(GRANT_FOR) Authorization grantFor,
        @JsonProperty(JUSTIFICATION) Markdown justification,
        @JsonProperty(REPLY_TO) ActorRef<GrantedDatasetAccess> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new GrantDatasetAccess(dataset, executor, grant, grantFor, justification, replyTo, errorTo);
    }

    public static GrantDatasetAccess apply(
        ResourcePath dataset, User executor, DatasetPrivilege grant, Authorization grantFor,
        ActorRef<GrantedDatasetAccess> replyTo, ActorRef<ErrorMessage> errorTo) {

        return new GrantDatasetAccess(dataset, executor, grant, grantFor, null, replyTo, errorTo);
    }

    public Optional<Markdown> getJustification() {
        return Optional.ofNullable(justification);
    }

}
