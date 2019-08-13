package maquette.controller.domain.exceptions;

public final class InvalidResourceNameException extends IllegalArgumentException implements DomainException {

    private InvalidResourceNameException(String message) {
        super(message);
    }

    public static InvalidResourceNameException apply(String name) {
        String message = String.format(
            "The provided resource name '%s' is not valid and cannot be transformed to a valid resource name.",
            name);

        return new InvalidResourceNameException(message);
    }

}
