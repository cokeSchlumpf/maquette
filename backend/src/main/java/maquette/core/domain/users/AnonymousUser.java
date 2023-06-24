package maquette.core.domain.users;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.domain.users.rbac.DomainPermission;

@Value
@AllArgsConstructor(staticName = "apply")
public class AnonymousUser implements User {

    @Override
    public boolean hasPermission(DomainPermission permission) {
        return false;
    }

    @Override
    public String getDisplayName() {
        return "(-(-_(-_-)_-)-)";
    }

}
