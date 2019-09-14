package maquette.controller.adapters.cli.commands;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateDatasetCmd implements Command {

    private final String namespace;

    private final String name;

    @JsonCreator
    public static CreateDatasetCmd apply(
        @JsonProperty("namespace") String namespace,
        @JsonProperty("name") String name) {

        return new CreateDatasetCmd(namespace, name);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ResourcePath rp;

        if (name == null) {
            return CompletableFuture.completedFuture(CommandResult.error("name of dataset must be specified"));
        }

        if (namespace == null) {
            rp = ResourcePath.apply(executor.getUserId().getId(), name);
        } else {
            rp = ResourcePath.apply(namespace, name);
        }

        return app
            .datasets()
            .createDataset(executor, rp)
            .thenApply(details -> CommandResult.success());
    }

}
