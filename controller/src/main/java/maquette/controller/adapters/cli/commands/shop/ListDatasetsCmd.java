package maquette.controller.adapters.cli.commands.shop;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.DataTable;
import maquette.controller.adapters.cli.DataTables;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.domain.CoreApplication;
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
            .shop()
            .listDatasets(executor)
            .thenApply(datasets -> {
                DataTable dt = DataTables.createDatasets(datasets);
                return CommandResult.success(dt.toAscii(), dt);
            });
    }

}
