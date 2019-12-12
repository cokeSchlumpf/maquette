package maquette.controller.domain.api.commands.projects;

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
import maquette.controller.domain.api.views.dataset.DatasetsVM;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListProjectDatasetsCmd implements Command {

    private static final String PROJECT = "project";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonCreator
    public static ListProjectDatasetsCmd apply(@JsonProperty(PROJECT) ResourceName project) {
        return new ListProjectDatasetsCmd(project);
    }

    @Override
    public CompletionStage<ViewModel> run(User executor, CoreApplication app,
                                          OutputFormat outputFormat) {

        ObjectValidation.notNull().validate(project, PROJECT);

        return app
            .projects()
            .getDatasets(executor, project)
            .thenApply(datasets -> DatasetsVM.apply(datasets, executor, outputFormat));
    }

}
