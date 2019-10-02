package maquette.controller.adapters.cli.commands.users;

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
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateUserDatasetCmd implements Command {

    private static final String DATASET = "dataset";
    private static final String IS_PRIVATE = "is-private";

    @JsonProperty(DATASET)
    private final String dataset;

    @JsonProperty(IS_PRIVATE)
    private final boolean isPrivate;

    @JsonCreator
    public static CreateUserDatasetCmd apply(
        @JsonProperty(DATASET) String dataset,
        @JsonProperty(IS_PRIVATE) boolean isPrivate) {

        return new CreateUserDatasetCmd(dataset, isPrivate);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(dataset, "dataset");

        return app
            .users()
            .createDataset(executor, ResourceName.apply(dataset), isPrivate)
            .thenApply(details -> CommandResult.success());
    }

}
