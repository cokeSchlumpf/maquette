package maquette.controller.application.commands.commands.shop;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FindProjectsCmd implements Command {

    private static final String QUERY = "query";

    @JsonProperty(QUERY)
    private final String query;

    @JsonCreator
    public static FindProjectsCmd apply(@JsonProperty(QUERY) String query) {
        return new FindProjectsCmd(query);
    }

    public static FindProjectsCmd apply() {
        return new FindProjectsCmd(null);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app, OutputFormat outputFormat) {
        return app
            .shop()
            .findProjects(executor, query)
            .thenApply(projects -> Operators.suppressExceptions(() -> {
                DataTable dt = DataTables.createProjects(projects);
                ProjectsVM vm = ProjectsVM.apply(projects, outputFormat);

                return CommandResult
                    .success(dt.toAscii(), dt)
                    .withView(vm);
            }));
    }

}
