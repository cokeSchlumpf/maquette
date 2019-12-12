package maquette.controller.domain.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import maquette.controller.domain.util.Operators;

public interface ViewModel {

    default CommandResult toCommandResult(ObjectMapper om) {
        String txt = Operators.suppressExceptions(() -> om.writeValueAsString(this));
        return CommandResult.success(txt);
    }

}
