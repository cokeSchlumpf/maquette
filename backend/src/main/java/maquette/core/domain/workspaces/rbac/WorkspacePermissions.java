package maquette.core.domain.workspaces.rbac;

import lombok.Value;
import maquette.core.domain.users.rbac.DomainPermission;

public final class WorkspacePermissions {

    private WorkspacePermissions() {

    }

    @Value
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
