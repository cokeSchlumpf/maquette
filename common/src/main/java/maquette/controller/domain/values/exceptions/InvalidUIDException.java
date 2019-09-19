package maquette.controller.domain.values.exceptions;

public final class InvalidUIDException extends IllegalArgumentException implements DomainException {

    private InvalidUIDException(String message) {
        super(message);
    }

    public static InvalidUIDException apply(String uid) {
        String message = String.format(
            "The provided UID '%s' is not valid.",
            uid);

        return new InvalidUIDException(message);
    }

}
