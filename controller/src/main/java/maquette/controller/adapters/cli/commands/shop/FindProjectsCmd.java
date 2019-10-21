package maquette.controller.adapters.cli.commands.shop;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.DataTable;
import maquette.controller.adapters.cli.DataTables;
import maquette.controller.adapters.cli.commands.Command;
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
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        return app
            .shop()
            .findProjects(executor, query)
            .thenApply(projects -> Operators.suppressExceptions(() -> {
                DataTable dt = DataTables.createProjects(projects);
                return CommandResult.success(dt.toAscii(), dt);
            }));
    }

}
