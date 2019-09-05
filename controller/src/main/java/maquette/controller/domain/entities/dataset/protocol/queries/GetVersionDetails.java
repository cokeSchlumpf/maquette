package maquette.controller.domain.entities.dataset.protocol.queries;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.VersionMessage;
import maquette.controller.domain.entities.dataset.protocol.results.GetVersionDetailsResult;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetVersionDetails implements DatasetMessage, VersionMessage {

    private static final String DATASET = "dataset";
    private static final String ERROR_TO = "error-to";
    private static final String REPLY_TO = "reply-to";
    private static final String VERSION_ID = "version-id";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(VERSION_ID)
    private final UID versionId;

    @JsonProperty(REPLY_TO)
    private final ActorRef<GetVersionDetailsResult> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static GetVersionDetails apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(VERSION_ID) UID versionId,
        @JsonProperty(REPLY_TO) ActorRef<GetVersionDetailsResult> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new GetVersionDetails(dataset, versionId, replyTo, errorTo);
    }

}
