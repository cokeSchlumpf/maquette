package maquette.controller.domain.values.core.governance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class GovernanceProperties {

    private static final String APPROVAL_REQUIRED = "approval-required";
    private static final String CLASSIFICATION = "classification";

    @JsonProperty(APPROVAL_REQUIRED)
    private final boolean approvalRequired;

    @JsonProperty(CLASSIFICATION)
    private final DataClassification classification;

    @JsonCreator
    public static GovernanceProperties apply(
        @JsonProperty(APPROVAL_REQUIRED) boolean approvalRequired,
        @JsonProperty(CLASSIFICATION) DataClassification classification) {

        return new GovernanceProperties(approvalRequired, classification);
    }

    public static GovernanceProperties apply() {
        return apply(true, DataClassification.SENSITIVE_PERSONAL_INFORMATION);
    }

}
