package maquette.controller.adapters.cli.commands.datasets;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.DataTables;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListDatasetsCmd implements Command {
    @JsonCreator
    public static ListDatasetsCmd apply() {
        return new ListDatasetsCmd();
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        return app
            .datasets()
            .listDatasets(executor)
            .thenApply(datasets -> CommandResult.success(DataTables.createDatasets(datasets)));
    }

}
