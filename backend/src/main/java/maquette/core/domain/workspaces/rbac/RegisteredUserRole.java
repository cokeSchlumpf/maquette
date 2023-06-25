package maquette.core.domain.workspaces.rbac;

import lombok.EqualsAndHashCode;
import lombok.Value;
import maquette.core.domain.users.rbac.DomainPermission;
import maquette.core.domain.users.rbac.DomainRole;

import java.util.Set;

/**
 * This role contains all permissions which should be assigned to registered users.
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class RegisteredUserRole implements DomainRole {

    public static String NAME = "/maquette/workspaces/roles/registered-user";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Set<DomainPermission> getPermissions() {
        return Set.of(
            new WorkspacePermissions.CreateWorkspaces()
        );
    }

}
