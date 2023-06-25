package maquette.core.domain.users.rbac;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.domain.workspaces.rbac.WorkspacePermissions;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CUSTOM,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(
        value = WorkspacePermissions.ManageWorkspace.class,
        name = WorkspacePermissions.ManageWorkspace.NAME),
    @JsonSubTypes.Type(
        value = WorkspacePermissions.ReadWorkspace.class,
        name = WorkspacePermissions.ReadWorkspace.NAME),
})
public interface DomainPermission {

}
