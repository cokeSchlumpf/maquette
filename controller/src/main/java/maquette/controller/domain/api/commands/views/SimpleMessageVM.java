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
public final class SimpleMessageVM implements ViewModel {

    private static final String MESSAGE = "message";

    @JsonProperty(MESSAGE)
    private final String message;

    @JsonCreator
    public static SimpleMessageVM apply(@JsonProperty(MESSAGE) String message) {
        return new SimpleMessageVM(message);
    }

    @Override
    public CommandResult toCommandResult(ObjectMapper om) {
        return CommandResult.success(message);
    }

}
