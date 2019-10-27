package maquette.controller.application.commands.commands.shop;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.application.commands.CommandResult;
import maquette.controller.application.commands.DataTable;
import maquette.controller.application.commands.DataTables;
import maquette.controller.application.commands.OutputFormat;
import maquette.controller.application.commands.commands.Command;
import maquette.controller.application.commands.views.ProjectsVM;
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
    public CompletionStage<CommandResult> run(User executor, CoreApplication app, OutputFormat outputFormat) {
        return app
            .shop()
            .listProjects(executor)
            .thenApply(projects -> {
                DataTable dt = DataTables.createProjects(projects);
                ProjectsVM vm = ProjectsVM.apply(projects, executor, outputFormat);

                return CommandResult
                    .success(dt.toAscii(), dt)
                    .withView(vm);
            });
    }

}
