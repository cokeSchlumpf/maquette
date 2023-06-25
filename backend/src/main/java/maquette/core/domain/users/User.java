package maquette.core.domain.users;

import maquette.core.application.MaquetteApplicationConfiguration;
import maquette.core.domain.users.exceptions.NotAuthorizedException;
import maquette.core.domain.users.rbac.DomainPermission;
import maquette.core.domain.users.rbac.DomainRole;

import java.util.Arrays;
import java.util.Set;

public interface User {

    Set<DomainRole> getRoles();

    /**
     * This method is intended to be called after loading the user from the database.
     * It will assign additional roles, based on application configuration.
     *
     * @param configuration The application configuration.
     */
    default void assignDomainRolesFromConfiguration(MaquetteApplicationConfiguration configuration) {

    }

    default boolean hasPermission(DomainPermission permission) {
        var allPermissions = this
            .getRoles()
            .stream()
            .flatMap(role -> role.getPermissions().stream())
            .toList();;

        return allPermissions
            .stream()
            .anyMatch(p -> p.equals(permission));
    }

    default boolean hasOneOfPermissions(DomainPermission ...permission) {
        return Arrays
            .stream(permission)
            .anyMatch(this::hasPermission);
    }

    default void requireOneOfPermission(DomainPermission ...permission) {
        if (!this.hasOneOfPermissions(permission)) {
            throw new NotAuthorizedException();
        }
    }

    default void requirePermission(DomainPermission permission) {
        requireOneOfPermission(permission);
    }

    String getDisplayName();

}
