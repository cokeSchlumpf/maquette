package maquette.controller.domain.api.commands.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.values.dataset.DatasetGrant;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor
public class DatasetAccessRequestVM {

    private final boolean canApprove;

    private final boolean canRevoke;

    private final String id;

    private final String initiatedBy;

    private final String initiated;

    private final String justification;

    private final String grant;

    private final AuthorizationVM grantFor;

    private final ApprovedVM approved;

    private final RevokedVM revoked;

    public static DatasetAccessRequestVM apply(DatasetGrant request, User executor, OutputFormat of) {
        // TODO
        return null;
    }

}
