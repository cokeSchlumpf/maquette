package maquette.controller.domain.entities.dataset.protocol.queries;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.results.GetDataResult;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetData implements DatasetMessage {

    private static final String EXECUTOR = "executor";
    private static final String REPLY_TO = "reply-to";
    private static final String ERROR_TO = "error-to";
    private static final String VERSION_ID = "version-id";

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(VERSION_ID)
    private final UID versionId;

    @JsonProperty(REPLY_TO)
    private final ActorRef<GetDataResult> replyTo;

    @JsonProperty(ERROR_TO)
    private final ActorRef<ErrorMessage> errorTo;

    @JsonCreator
    public static GetData apply(
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(VERSION_ID) UID versionId,
        @JsonProperty(REPLY_TO) ActorRef<GetDataResult> replyTo,
        @JsonProperty(ERROR_TO) ActorRef<ErrorMessage> errorTo) {

        return new GetData(executor, versionId, replyTo, errorTo);
    }

}
