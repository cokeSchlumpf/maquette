package maquette.core.domain.workspaces;

import lombok.EqualsAndHashCode;
import lombok.Value;
import maquette.core.domain.users.rbac.DomainPermission;
import maquette.core.domain.users.rbac.DomainRole;

import java.util.Set;

public final class WorkspaceRoles {

    private WorkspaceRoles() {

    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class WorkspaceOwner implements DomainRole {

        String workspace;

        public static String NAME = "/maquette/workspaces/roles/owner";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public Set<DomainPermission> getPermissions() {
            return Set.of(
                new WorkspacePermissions.ManageWorkspace(workspace),
                new WorkspacePermissions.ReadWorkspace(workspace)
            );
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class WorkspaceContributor implements DomainRole {

        String workspace;

        public static String NAME = "/maquette/workspaces/roles/member";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public Set<DomainPermission> getPermissions() {
            return Set.of(
                new WorkspacePermissions.ReadWorkspace(workspace)
            );
        }

    }

}
