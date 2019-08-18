package maquette.controller.domain.exceptions;

import maquette.controller.domain.values.iam.ErrorMessage;

public final class AskCompletionException extends RuntimeException {

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
