package maquette.core.domain.users;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.domain.users.rbac.DomainPermission;
import maquette.core.domain.users.rbac.DomainRole;

import java.util.Set;

@Value
@AllArgsConstructor(staticName = "apply")
public class AnonymousUser implements User {

    @Override
    public Set<DomainRole> getRoles() {
        return Set.of();
    }

    @Override
    public String getDisplayName() {
        return "(-(-_(-_-)_-)-)";
    }

}
