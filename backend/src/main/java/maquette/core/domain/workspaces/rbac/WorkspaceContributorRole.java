package maquette.core.domain.workspaces.rbac;

import lombok.EqualsAndHashCode;
import lombok.Value;
import maquette.core.domain.users.rbac.DomainPermission;
import maquette.core.domain.users.rbac.DomainRole;

import java.util.Set;

@Value
@EqualsAndHashCode(callSuper = false)
public class WorkspaceContributorRole implements DomainRole {

    String workspace;

    public static String NAME = "/maquette/workspaces/roles/contributor";

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
