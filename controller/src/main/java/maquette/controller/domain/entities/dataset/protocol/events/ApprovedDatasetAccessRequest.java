package maquette.controller.domain.entities.dataset.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.values.core.governance.Approved;
import maquette.controller.domain.values.dataset.DatasetAccessRequest;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApprovedDatasetAccessRequest implements DatasetEvent {

    private static final String REQUEST = "request";
    private static final String APPROVAL = "approval";

    @JsonProperty(REQUEST)
    private final DatasetAccessRequest request;

    @JsonProperty(APPROVAL)
    private final Approved approval;

    @JsonCreator
    public static ApprovedDatasetAccessRequest apply(
        @JsonProperty(REQUEST) DatasetAccessRequest request,
        @JsonProperty(APPROVAL) Approved approval) {

        return new ApprovedDatasetAccessRequest(request, approval);
    }

}
