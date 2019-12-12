package maquette.controller.domain.api.views.dataset;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.OutputFormat;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatasetAccessRequestsVM {

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
    public static DatasetAccessRequestsVM apply(
        @JsonProperty(OWN_REQUESTS) List<DatasetAccessRequestVM> ownRequests,
        @JsonProperty(USER_REQUESTS) List<DatasetAccessRequestVM> userRequests,
        @JsonProperty(CAN_CREATE_ACCESS_REQUEST) boolean canCreateAccessRequest,
        @JsonProperty(CAN_MANAGE_ACCESS_REQUESTS) boolean canManageAccessRequests,
        @JsonProperty(OPEN_REQUESTS) int openRequests) {

        return new DatasetAccessRequestsVM(ownRequests, userRequests, canCreateAccessRequest, canManageAccessRequests, openRequests);
    }

    public static DatasetAccessRequestsVM apply(User executor, DatasetDetails details, OutputFormat of) {
        int openRequests = 0;
        boolean canCreateAccessRequest = !(details.getAcl().canConsume(executor) || details.getAcl().canProduce(executor));
        boolean canManageAccessRequests = details.getAcl().canManage(executor);
        List<DatasetAccessRequestVM> userAccessRequests = Lists.newArrayList();
        List<DatasetAccessRequestVM> ownAccessRequests = Lists.newArrayList();

        if (canManageAccessRequests) {
            userAccessRequests = details
                .getAcl()
                .getGrants()
                .stream()
                .map(grant -> DatasetAccessRequestVM.apply(grant, details, executor, of))
                .collect(Collectors.toList());

            openRequests = details.getAcl().getOpenGrants().size();
        }

        if (canCreateAccessRequest) {
            userAccessRequests = details
                .getAcl()
                .getOpenGrants()
                .stream()
                .map(grant -> DatasetAccessRequestVM.apply(grant, details, executor, of))
                .collect(Collectors.toList());
        }

        return apply(ownAccessRequests, userAccessRequests, canCreateAccessRequest, canManageAccessRequests, openRequests);
    }

}
