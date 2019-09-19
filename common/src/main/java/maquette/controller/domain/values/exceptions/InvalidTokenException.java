package maquette.controller.domain.values.exceptions;

import maquette.controller.domain.values.core.UID;

public final class InvalidTokenException extends IllegalArgumentException implements DomainException {

    private InvalidTokenException(String message) {
        super(message);
    }

    public static InvalidTokenException apply(String user) {
        String message = String.format(
            "The provided token is not valid for user '%s'.",
            user);

        return new InvalidTokenException(message);
    }

}
