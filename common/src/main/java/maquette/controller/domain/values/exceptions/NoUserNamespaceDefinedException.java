package maquette.controller.domain.values.exceptions;

import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserId;

public final class NoUserNamespaceDefinedException extends RuntimeException implements DomainException {

    private NoUserNamespaceDefinedException(String message) {
        super(message);
    }

    public static NoUserNamespaceDefinedException apply(UserId id) {
        String message = String.format(
            "`%s` has no default namespace configured",
            id.getId());

        return new NoUserNamespaceDefinedException(message);
    }

}
