package maquette.controller.domain.entities.dataset.protocol.commands;

import org.apache.avro.Schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.VersionMessage;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetVersion;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateDatasetVersion implements DatasetMessage, VersionMessage {

    private static final String EXECUTOR = "executor";
    private static final String DATASET = "dataset";
    private static final String REPLY_TO = "reply-to";
    private static final String SCHEMA = "schema";
    private static final String ERROR_TO = "error-to";

    @JsonProperty(EXECUTOR)
    public final User executor;

    @JsonProperty(DATASET)
    public final ResourcePath dataset;

    @JsonProperty(SCHEMA)
    public final Schema schema;

    @JsonProperty(REPLY_TO)
    public final ActorRef<CreatedDatasetVersion> replyTo;

    @JsonProperty(ERROR_TO)
    public final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static CreateDatasetVersion apply(
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(SCHEMA) Schema schema,
        @JsonProperty(REPLY_TO) ActorRef<CreatedDatasetVersion> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new CreateDatasetVersion(executor, dataset, schema, replyTo, errorTo);
    }

}
