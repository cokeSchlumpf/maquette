package maquette.controller.adapters.cli.commands.datasets;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.adapters.cli.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserId;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateDatasetProducerToken implements Command {

    private final String namespace;

    private final String dataset;

    private final String forUser;

    @JsonCreator
    public static CreateDatasetProducerToken apply(
        @JsonProperty("namespace") String namespace,
        @JsonProperty("dataset") String dataset,
        @JsonProperty("for-user") String forUser) {

        return new CreateDatasetProducerToken(namespace, dataset, forUser);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(dataset, "dataset");
        ResourcePath datasetResource = ResourcePath.apply(executor, namespace, dataset);

        UserId forUserId;

        if (forUser == null) {
            forUserId = executor.getUserId();
        } else {
            forUserId = UserId.apply(forUser);
        }

        return app
            .datasets()
            .createDatasetProducerToken(executor, forUserId, datasetResource)
            .thenApply(token -> {
                String sb = String.format(
                    "Created producer token '%s' with secret '%s'",
                    token.getDetails().getName(),
                    token.getSecret());

                return CommandResult.success(sb);
            });
    }

}
