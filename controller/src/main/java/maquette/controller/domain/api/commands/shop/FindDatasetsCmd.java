package maquette.controller.domain.api.commands.shop;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.api.OutputFormat;
import maquette.controller.domain.api.ViewModel;
import maquette.controller.domain.api.Command;
import maquette.controller.domain.api.views.dataset.DatasetsVM;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FindDatasetsCmd implements Command {

    private static final String QUERY = "query";

    @JsonProperty(QUERY)
    private final String query;

    @JsonCreator
    public static FindDatasetsCmd apply(
        @JsonProperty(QUERY) String query) {

        return new FindDatasetsCmd(query);
    }

    public static FindDatasetsCmd apply() {
        return apply(null);
    }

    @Override
    public CompletionStage<ViewModel> run(User executor, CoreApplication app,
                                          OutputFormat outputFormat) {
        return app
            .shop()
            .findDatasets(executor, query)
            .thenApply(datasets -> DatasetsVM.apply(datasets, executor, outputFormat));
    }

}
