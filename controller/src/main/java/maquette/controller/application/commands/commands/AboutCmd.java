package maquette.controller.application.commands.commands;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.application.commands.CommandResult;
import maquette.controller.application.commands.OutputFormat;
import maquette.controller.application.commands.views.ApplicationVM;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AboutCmd implements Command {

    @JsonCreator
    public static AboutCmd apply() {
        return new AboutCmd();
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app,
                                              OutputFormat outputFormat) {
        String about = "IBM Maquette Data Platform, v0.0.42, local";
        ApplicationVM vm = ApplicationVM.apply("IBM Maquette", "Data Platform", "v0.0.42", "local");

        return CompletableFuture.completedFuture(
            CommandResult
                .success(about)
                .withView(vm));
    }
}
