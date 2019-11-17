package maquette.controller.domain.values.exceptions;

public final class DatasetGrantAlreadyDecidedException extends IllegalArgumentException implements DomainException {

    private DatasetGrantAlreadyDecidedException(String message) {
        super(message);
    }

    public static DatasetGrantAlreadyDecidedException apply() {
        String message = "The dataset access request has been already approved or rejected.";
        return new DatasetGrantAlreadyDecidedException(message);
    }

}
