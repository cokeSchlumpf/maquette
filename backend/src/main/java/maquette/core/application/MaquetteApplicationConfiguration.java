package maquette.core.application;

import lombok.Data;
import maquette.core.domain.users.rbac.DomainRole;

import java.util.Set;

@Data
public class MaquetteApplicationConfiguration {

    /**
     * Default permissions for registered users.
     */
    Set<DomainRole> registeredUsersDefaultRoles;

}
