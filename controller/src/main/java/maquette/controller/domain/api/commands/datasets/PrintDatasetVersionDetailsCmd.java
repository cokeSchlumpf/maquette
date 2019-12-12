package maquette.controller.domain.api.commands.datasets;

import java.util.Objects;
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
import maquette.controller.domain.api.views.dataset.DatasetVersionVM;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PrintDatasetVersionDetailsCmd implements Command {

    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String VERSION = "version";

    @JsonProperty(PROJECT)
    private final String project;

    @JsonProperty(DATASET)
    private final String dataset;

    @JsonProperty(VERSION)
    private final String version;

    @JsonCreator
    public static PrintDatasetVersionDetailsCmd apply(
        @JsonProperty(PROJECT) String project,
        @JsonProperty(DATASET) String dataset,
        @JsonProperty(VERSION) String version) {
        
        return new PrintDatasetVersionDetailsCmd(project, dataset, version);
    }

    @Override
    public CompletionStage<ViewModel> run(User executor, CoreApplication app,
                                          OutputFormat outputFormat) {
        ObjectValidation.notNull().validate(dataset, DATASET);
        ResourcePath datasetResource = ResourcePath.apply(executor, project, dataset);


        CompletionStage<VersionDetails> versionDetails;

        if (Objects.isNull(version)) {
            versionDetails = app
                .datasets()
                .getVersionDetails(executor, datasetResource);
        } else {
            versionDetails = app
                .datasets()
                .getVersionDetails(executor, datasetResource, VersionTag.apply(version));
        }

        return versionDetails
            .thenApply(details -> DatasetVersionVM.apply(details, outputFormat));
    }

}
