package maquette.controller.application.commands.commands.projects;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.application.commands.CommandResult;
import maquette.controller.application.commands.DataTables;
import maquette.controller.application.commands.OutputFormat;
import maquette.controller.application.commands.commands.Command;
import maquette.controller.application.commands.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
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
    public CompletionStage<CommandResult> run(User executor, CoreApplication app,
                                              OutputFormat outputFormat) {
        ObjectValidation.notNull().validate(project, PROJECT);

        return app
            .projects()
            .getDatasets(executor, project)
            .thenApply(datasets -> CommandResult.success(DataTables.createDatasets(datasets)));
    }

}
