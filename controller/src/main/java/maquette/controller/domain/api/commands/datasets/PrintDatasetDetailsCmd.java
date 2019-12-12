package maquette.controller.domain.api.commands.datasets;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.api.OutputFormat;
import maquette.controller.domain.api.ViewModel;
import maquette.controller.domain.api.Command;
import maquette.controller.domain.api.validations.ObjectValidation;
import maquette.controller.domain.api.views.dataset.DatasetVM;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PrintDatasetDetailsCmd implements Command {

    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(DATASET)
    private final ResourceName dataset;

    @JsonCreator
    public static PrintDatasetDetailsCmd apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(DATASET) ResourceName dataset) {
        return new PrintDatasetDetailsCmd(project, dataset);
    }

    @Override
    public CompletionStage<ViewModel> run(User executor, CoreApplication app,
                                          OutputFormat outputFormat) {
        ObjectValidation.notNull().validate(dataset, DATASET);
        ResourcePath datasetResource = ResourcePath.apply(executor, project, dataset);

        return Operators.compose(
            app.datasets().getDetails(executor, datasetResource),
            app.projects().getDetails(executor, datasetResource.getProject()),
            (details, project) -> DatasetVM.apply(details, project, executor, outputFormat));
    }

}
