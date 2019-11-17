package maquette.controller.domain.values.core.governance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.Executed;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Approved {

    private static final String EXECUTED = "executed";
    private static final String COMMENT = "comment";

    @JsonProperty(EXECUTED)
    private final Executed executed;

    @JsonProperty(COMMENT)
    private final String comment;

    @JsonCreator
    public static Approved apply(
        @JsonProperty(EXECUTED) Executed executed,
        @JsonProperty(COMMENT) String comment) {

        return new Approved(executed, comment);
    }

}
