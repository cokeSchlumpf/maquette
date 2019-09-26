package maquette.controller.adapters.cli.commands.datasets;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.adapters.cli.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateDatasetCmd implements Command {

    private final String namespace;

    private final String dataset;

    @JsonCreator
    public static CreateDatasetCmd apply(
        @JsonProperty("namespace") String namespace,
        @JsonProperty("dataset") String dataset) {

        return new CreateDatasetCmd(namespace, dataset);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(dataset, "dataset");
        ResourcePath rp = ResourcePath.apply(executor, namespace, dataset);

        return app
            .datasets()
            .createDataset(executor, rp, false)
            .thenApply(details -> CommandResult.success());
    }

}
