package maquette.core.domain.workspaces;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.domain.users.rbac.DomainPermission;

public final class WorkspacePermissions {

    private WorkspacePermissions() {

    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class CreateWorkspaces implements DomainPermission {

        public static final String NAME = "/maquette/workspaces/permissions/create";

    }

    public record ManageWorkspace(String workspace) implements DomainPermission {

        public static final String NAME = "/maquette/workspace/permissions/manage";

    }

    public record ReadWorkspace(String workspace) implements DomainPermission {

        public static final String NAME = "/maquette/workspace/permissions/read";

    }

}
