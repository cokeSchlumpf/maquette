package maquette.controller.domain.values.exceptions;

public final class DatasetGrantAlreadyRevokedException extends IllegalArgumentException implements DomainException {

    private DatasetGrantAlreadyRevokedException(String message) {
        super(message);
    }

    public static DatasetGrantAlreadyRevokedException apply() {
        String message = "The dataset access request has been already revoked.";
        return new DatasetGrantAlreadyRevokedException(message);
    }

}
