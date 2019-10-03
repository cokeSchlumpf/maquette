package maquette.controller.adapters.cli.commands.users;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.DataTable;
import maquette.controller.adapters.cli.DataTables;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ListUserDatasetsCmd implements Command {

    @JsonCreator
    public static ListUserDatasetsCmd apply() {
        return new ListUserDatasetsCmd();
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        return app
            .users()
            .getDatasets(executor)
            .thenApply(datasets -> {
                DataTable dt = DataTables.createDatasets(datasets);
                return CommandResult.success(dt.toAscii(), dt);
            });
    }

}
