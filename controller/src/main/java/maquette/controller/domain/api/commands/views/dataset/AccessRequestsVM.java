package maquette.controller.domain.api.commands.views.dataset;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccessRequestsVM {

    private static final String CAN_CREATE_ACCESS_REQUEST = "can-create-access-request";
    private static final String CAN_MANAGE_ACCESS_REQUESTS = "can-manage-access-requests";
    private static final String OPEN_REQUESTS = "open-requests";
    private static final String OWN_REQUESTS = "own-requests";
    private static final String USER_REQUESTS = "user-requests";

    @JsonProperty(OWN_REQUESTS)
    private final List<DatasetAccessRequestVM> ownRequests;

    @JsonProperty(USER_REQUESTS)
    private final List<DatasetAccessRequestVM> userRequests;

    @JsonProperty(CAN_CREATE_ACCESS_REQUEST)
    private final boolean canCreateAccessRequest;

    @JsonProperty(CAN_MANAGE_ACCESS_REQUESTS)
    private final boolean canManageAccessRequests;

    @JsonProperty(OPEN_REQUESTS)
    private final int openRequests;

    @JsonCreator
    public static AccessRequestsVM apply(
        @JsonProperty(OWN_REQUESTS) List<DatasetAccessRequestVM> ownRequests,
        @JsonProperty(USER_REQUESTS) List<DatasetAccessRequestVM> userRequests,
        @JsonProperty(CAN_CREATE_ACCESS_REQUEST) boolean canCreateAccessRequest,
        @JsonProperty(CAN_MANAGE_ACCESS_REQUESTS) boolean canManageAccessRequests,
        @JsonProperty(OPEN_REQUESTS) int openRequests) {

        return new AccessRequestsVM(ownRequests, userRequests, canCreateAccessRequest, canManageAccessRequests, openRequests);
    }

}
