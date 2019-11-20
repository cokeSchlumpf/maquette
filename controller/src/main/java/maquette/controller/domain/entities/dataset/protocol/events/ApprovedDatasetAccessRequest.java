package maquette.controller.domain.entities.dataset.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.values.dataset.DatasetGrant;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApprovedDatasetAccessRequest implements DatasetEvent {

    private static final String GRANT = "grant";

    @JsonProperty(GRANT)
    private final DatasetGrant grant;

    @JsonCreator
    public static ApprovedDatasetAccessRequest apply(
        @JsonProperty(GRANT) DatasetGrant grant) {

        return new ApprovedDatasetAccessRequest(grant);
    }

}
