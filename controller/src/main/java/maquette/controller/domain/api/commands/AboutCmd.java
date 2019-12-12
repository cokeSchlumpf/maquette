package maquette.controller.domain.api.commands;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.api.Command;
import maquette.controller.domain.api.OutputFormat;
import maquette.controller.domain.api.ViewModel;
import maquette.controller.domain.api.views.ApplicationVM;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AboutCmd implements Command {

    @JsonCreator
    public static AboutCmd apply() {
        return new AboutCmd();
    }

    @Override
    public CompletionStage<ViewModel> run(User executor, CoreApplication app,
                                          OutputFormat outputFormat) {
        return CompletableFuture.completedFuture(ApplicationVM.apply("IBM Maquette", "Data Platform", "v0.0.42", "local"));
    }
}
