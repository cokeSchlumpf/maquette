package maquette.controller.domain.values.exceptions;

public final class InvalidAvroFileException extends RuntimeException implements DomainException {

    private InvalidAvroFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidAvroFileException apply(Throwable cause) {
        String message = "The provided file cannot be read. Is it a valid avro file?";

        return new InvalidAvroFileException(message, cause);
    }

}
