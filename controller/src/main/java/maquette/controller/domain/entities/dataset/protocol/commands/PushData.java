package maquette.controller.domain.entities.dataset.protocol.commands;

import java.util.List;

import org.apache.avro.generic.GenericData;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.VersionMessage;
import maquette.controller.domain.entities.dataset.protocol.events.PushedData;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PushData implements DatasetMessage, VersionMessage {

    private static final String EXECUTOR = "executor";
    private static final String RECORDS = "records";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";
    private static final String VERSION_ID = "version-id";

    @JsonProperty(VERSION_ID)
    private final UID versionId;

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(RECORDS)
    private final List<GenericData.Record> records;

    @JsonProperty(REPLY_TO)
    private final ActorRef<PushedData> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static PushData apply(
        @JsonProperty(VERSION_ID) UID versionId,
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(RECORDS) List<GenericData.Record> records,
        @JsonProperty(REPLY_TO) ActorRef<PushedData> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new PushData(versionId, executor, records, replyTo, errorTo);
    }

}
