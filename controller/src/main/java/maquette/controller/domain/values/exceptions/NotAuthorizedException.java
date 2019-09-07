package maquette.controller.domain.values.exceptions;

import maquette.controller.domain.values.iam.User;

public final class NotAuthorizedException extends RuntimeException implements DomainException {

    private NotAuthorizedException(String message) {
        super(message);
    }

    public static NotAuthorizedException apply(User user) {
        String message = String.format(
            "`%s` is not authorized to perform this action",
            user.getUserId().getId());

        return new NotAuthorizedException(message);
    }

}
