package maquette.controller.application.commands.commands.datasets;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.application.commands.CommandResult;
import maquette.controller.application.commands.commands.Command;
import maquette.controller.application.commands.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeDatasetDescriptionCmd implements Command {

    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String DESCRIPTION = "description";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(DATASET)
    private final ResourceName dataset;

    @JsonProperty(DESCRIPTION)
    private final Markdown description;

    @JsonCreator
    public static ChangeDatasetDescriptionCmd apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(DATASET) ResourceName dataset,
        @JsonProperty(DESCRIPTION) Markdown description) {

        return new ChangeDatasetDescriptionCmd(project, dataset, description);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(dataset, DATASET);
        ObjectValidation.notNull().validate(description, DESCRIPTION);

        ResourcePath rp = ResourcePath.apply(executor, project, dataset);

        return app
            .datasets()
            .changeDescription(executor,rp, description)
            .thenApply(details -> CommandResult.success());
    }

}
