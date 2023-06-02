package maquette.core.domain.workspaces.exceptions;

import maquette.common.DomainException;

public class WorkspaceAlreadyExistsException extends DomainException {

    private WorkspaceAlreadyExistsException(String message) {
        super(message);
    }

    public static WorkspaceAlreadyExistsException apply(String workspace) {
        var msg = String.format("A workspace with the name `%s` already exists.", workspace);
        return new WorkspaceAlreadyExistsException(msg);
    }

}
