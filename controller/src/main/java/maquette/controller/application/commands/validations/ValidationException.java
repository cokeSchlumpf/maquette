package maquette.controller.application.commands.validations;

import maquette.controller.domain.values.exceptions.DomainException;

public final class ValidationException extends IllegalArgumentException implements DomainException {

    private ValidationException(String message) {
        super(message);
    }

    public static ValidationException apply(String message, Object... args) {
        return new ValidationException(String.format(message, args));
    }

}
