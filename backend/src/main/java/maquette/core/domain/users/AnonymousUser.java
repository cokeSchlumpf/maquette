package maquette.core.domain.users;

import maquette.core.domain.users.rbac.DomainPermission;

public final class AnonymousUser implements User {

    @Override
    public boolean hasPermission(DomainPermission permission) {
        return false;
    }

    @Override
    public String getDisplayName() {
        return "(-(-_(-_-)_-)-)";
    }

}
