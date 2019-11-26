package maquette.controller.domain.api.commands.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.CommandResult;
import maquette.controller.domain.api.commands.ViewModel;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SuccessVM implements ViewModel {

    @JsonCreator
    public static SuccessVM apply() {
        return new SuccessVM();
    }

    @Override
    public CommandResult toCommandResult(ObjectMapper om) {
        return CommandResult.success("Ok");
    }

}
