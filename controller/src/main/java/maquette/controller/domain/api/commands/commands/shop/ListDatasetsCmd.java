package maquette.controller.domain.api.commands.commands.shop;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.domain.api.commands.CommandResult;
import maquette.controller.domain.api.commands.DataTable;
import maquette.controller.domain.api.commands.DataTables;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.ViewModel;
import maquette.controller.domain.api.commands.commands.Command;
import maquette.controller.domain.api.commands.views.dataset.DatasetsVM;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListDatasetsCmd implements Command {
    @JsonCreator
    public static ListDatasetsCmd apply() {
        return new ListDatasetsCmd();
    }

    @Override
    public CompletionStage<ViewModel> run(
        User executor, CoreApplication app, OutputFormat outputFormat) {

        return app
            .shop()
            .listDatasets(executor)
            .thenApply(datasets -> DatasetsVM.apply(datasets, executor, outputFormat));
    }

}
