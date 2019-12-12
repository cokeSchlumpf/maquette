package maquette.controller.domain.api.views.dataset;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.OutputFormat;
import maquette.controller.domain.api.views.ApprovedVM;
import maquette.controller.domain.api.views.AuthorizationVM;
import maquette.controller.domain.api.views.RevokedVM;
import maquette.controller.domain.values.core.Executed;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.governance.AccessRequest;
import maquette.controller.domain.values.dataset.DatasetDetails;
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

    public static DatasetAccessRequestVM apply(DatasetGrant request, DatasetDetails dsDetails, User executor, OutputFormat of) {
        return new DatasetAccessRequestVM(
            dsDetails.getAcl().canManage(executor),
            dsDetails.getAcl().canManage(executor),
            of.format(request.getId()),
            of.format(request.getRequest().map(AccessRequest::getExecuted).map(Executed::getBy)),
            of.format(request.getRequest().map(AccessRequest::getExecuted).map(Executed::getAt)),
            of.format(request.getRequest().map(AccessRequest::getJustification).map(Markdown::asPlainText)),
            of.format(request.getGrant()),
            AuthorizationVM.apply(request.getGrantFor()),
            null,
            null);
    }

}
