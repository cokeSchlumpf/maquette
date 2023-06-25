package maquette.core.domain.users.rbac;

import java.util.Set;

public class EmptyRole implements DomainRole {
    @Override
    public String getName() {
        return "maquette/empty-role";
    }

    @Override
    public Set<DomainPermission> getPermissions() {
        return Set.of();
    }
}
