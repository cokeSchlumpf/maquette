package maquette.core.domain.users.exceptions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.common.DomainException;
import maquette.core.domain.workspaces.rbac.WorkspacePermissions;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CUSTOM,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "name"
)
@JsonSubTypes({
    @JsonSubTypes.Type(
        value = WorkspacePermissions.ManageWorkspace.class,
        name = WorkspacePermissions.ManageWorkspace.NAME),
    @JsonSubTypes.Type(
        value = WorkspacePermissions.ReadWorkspace.class,
        name = WorkspacePermissions.ReadWorkspace.NAME),
})
public class NotAuthorizedException extends DomainException {

    private NotAuthorizedException(String message) {
        super(message, 401);
    }

    public NotAuthorizedException() {
        this("You are not authorized to execute this operation.");
    }

}
