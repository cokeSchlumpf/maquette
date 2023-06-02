package maquette.core.domain.users;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.domain.users.rbac.DomainPermission;

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
    public boolean hasPermission(DomainPermission permission) {
        return false;
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + lastName;
    }

}
