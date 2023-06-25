package maquette.core.domain.users;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.domain.users.rbac.DomainPermission;
import maquette.core.domain.users.rbac.DomainRole;

import java.util.Set;

@Value
@AllArgsConstructor(staticName = "apply")
public class AuthenticatedUser implements User {

    /**
     * The unique email-address of the user.
     */
    String email;

    /**
     * The first name of the user.
     */
    String firstName;

    /**
     * The last name of the user.
     */
    String lastName;

    @Override
    public Set<DomainRole> getRoles() {
        return Set.of();
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + lastName;
    }

}
