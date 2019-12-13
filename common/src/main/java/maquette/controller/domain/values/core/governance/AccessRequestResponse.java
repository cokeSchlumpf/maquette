package maquette.controller.domain.values.core.governance;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import maquette.controller.domain.values.core.Executed;
import maquette.controller.domain.values.core.Markdown;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = Approved.class, name = "approved"),
        @JsonSubTypes.Type(value = Rejected.class, name = "rejected")
    })
@ToString
@EqualsAndHashCode
public abstract class AccessRequestResponse {

    private final Executed executed;

    private final Markdown justification;

    private AccessRequestResponse(Executed executed, Markdown justification) {
        this.executed = executed;
        this.justification = justification;
    }

    public Executed getExecuted() {
        return executed;
    }

    public Markdown getJustification() {
        return justification;
    }

    public abstract boolean isApproved();

    public static AccessRequestResponse approved(Executed executed, Markdown justification) {
        return new Approved(executed, justification);
    }

    public static AccessRequestResponse rejected(Executed executed, Markdown justification) {
        return new Rejected(executed, justification);
    }

    private static class Approved extends AccessRequestResponse {

        private Approved(Executed executed, Markdown justification) {
            super(executed, justification);
        }

        @Override
        public boolean isApproved() {
            return true;
        }

    }

    private static class Rejected extends AccessRequestResponse {

        private Rejected(Executed executed, Markdown justification) {
            super(executed, justification);
        }

        @Override
        public boolean isApproved() {
            return false;
        }

    }

}
