package maquette.core.domain.users;

import maquette.core.domain.users.exceptions.NotAuthorizedException;
import maquette.core.domain.users.rbac.DomainPermission;

import java.util.Arrays;

sealed public interface User permits AnonymousUser, AuthenticatedUser, RegisteredUser {

    boolean hasPermission(DomainPermission permission);

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
