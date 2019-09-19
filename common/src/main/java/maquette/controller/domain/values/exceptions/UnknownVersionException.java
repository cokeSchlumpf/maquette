package maquette.controller.domain.values.exceptions;

import maquette.controller.domain.values.dataset.VersionTag;

public final class UnknownVersionException extends IllegalArgumentException implements DomainException {

    private UnknownVersionException(String message) {
        super(message);
    }

    public static UnknownVersionException apply(VersionTag version) {
        String message = String.format(
            "The provided version '%s' does not exist in dataset.",
            version.toString());

        return new UnknownVersionException(message);
    }

}
