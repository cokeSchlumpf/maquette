package maquette.controller.domain.values.core.governance;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.Executed;
import maquette.controller.domain.values.core.Markdown;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessRevoke {

    private static final String EXECUTED = "executed";
    private static final String JUSTIFICATION = "justification";

    @JsonProperty(EXECUTED)
    private final Executed executed;

    @JsonProperty(JUSTIFICATION)
    private final Markdown justification;

    @JsonCreator
    public static AccessRevoke apply(
        @JsonProperty(EXECUTED) Executed executed,
        @JsonProperty(JUSTIFICATION) Markdown justification) {

        return new AccessRevoke(executed, justification);
    }

    public static AccessRevoke apply(Executed executed) {
        return apply(executed, null);
    }

    public Optional<Markdown> getJustification() {
        return Optional.ofNullable(justification);
    }

}
