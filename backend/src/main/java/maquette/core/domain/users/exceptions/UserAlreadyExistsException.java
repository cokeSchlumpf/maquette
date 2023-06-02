package maquette.core.domain.users.exceptions;

import maquette.common.DomainException;

public class UserAlreadyExistsException extends DomainException {

    private UserAlreadyExistsException() {
        super("User already exists.");
    }

    public static UserAlreadyExistsException apply() {
        return new UserAlreadyExistsException();
    }

}
