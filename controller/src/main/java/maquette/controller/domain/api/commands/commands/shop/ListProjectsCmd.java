package maquette.controller.domain.api.commands.commands.shop;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.CommandResult;
import maquette.controller.domain.api.commands.DataTable;
import maquette.controller.domain.api.commands.DataTables;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.ViewModel;
import maquette.controller.domain.api.commands.commands.Command;
import maquette.controller.domain.api.commands.views.ProjectsVM;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListProjectsCmd implements Command {

    @JsonCreator
    public static ListProjectsCmd apply() {
        return new ListProjectsCmd();
    }

    @Override
    public CompletionStage<ViewModel> run(User executor, CoreApplication app, OutputFormat outputFormat) {
        return app
            .shop()
            .listProjects(executor)
            .thenApply(projects -> ProjectsVM.apply(projects, executor, outputFormat));
    }

}
