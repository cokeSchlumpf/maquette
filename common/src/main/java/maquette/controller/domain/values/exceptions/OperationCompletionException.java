package maquette.controller.domain.values.exceptions;

import maquette.controller.domain.values.core.ErrorMessage;

public final class OperationCompletionException extends RuntimeException implements DomainException {

    private final ErrorMessage error;

    private OperationCompletionException(ErrorMessage error) {
        super(error.getMessage());
        this.error = error;
    }

    public static OperationCompletionException apply(ErrorMessage error) {
        return new OperationCompletionException(error);
    }

    public ErrorMessage getError() {
        return error;
    }
}
