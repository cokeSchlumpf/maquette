package maquette.controller.domain.exceptions;

import maquette.controller.domain.values.core.ErrorMessage;

public final class AskCompletionException extends RuntimeException implements DomainException {

    private final ErrorMessage error;

    private AskCompletionException(ErrorMessage error) {
        super(error.getMessage());
        this.error = error;
    }

    public static AskCompletionException apply(ErrorMessage error) {
        return new AskCompletionException(error);
    }

    public ErrorMessage getError() {
        return error;
    }
}
