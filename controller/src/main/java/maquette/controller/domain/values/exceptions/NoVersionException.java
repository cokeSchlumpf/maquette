package maquette.controller.domain.values.exceptions;

public final class NoVersionException extends IllegalArgumentException implements DomainException {

    private NoVersionException(String message) {
        super(message);
    }

    public static NoVersionException apply() {
        return new NoVersionException("The dataset does not contain a version.");
    }

}
