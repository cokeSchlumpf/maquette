package maquette.controller.domain.values.core.governance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.Executed;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessRequest {

    private static final String EXECUTED = "executed";
    private static final String JUSTIFICATION = "justification";

    @JsonProperty(EXECUTED)
    private final Executed executed;

    @JsonProperty(JUSTIFICATION)
    private final Markdown justification;

    @JsonCreator
    public static AccessRequest apply(
        @JsonProperty(EXECUTED) Executed executed,
        @JsonProperty(JUSTIFICATION) Markdown justification) {

        return new AccessRequest(executed, justification);
    }

    public static AccessRequest fake() {
        var executed = Executed.now(UserId.random());
        return apply(executed, Markdown.lorem());
    }

}
