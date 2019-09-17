package maquette.controller.adapters.cli.commands.namespaces;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.DataTable;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListDatasetsCmd implements Command {

    private final String namespace;

    @JsonCreator
    public static ListDatasetsCmd apply(@JsonProperty("namespace") String namespace) {
        return new ListDatasetsCmd(namespace);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ResourceName resource;

        if (namespace == null) {
            resource = ResourceName.apply(executor.getUserId().getId());
        } else {
            resource = ResourceName.apply(namespace);
        }

        return app
            .namespaces()
            .getNamespaceDetails(executor, resource)
            .thenApply(details -> {
                DataTable dt = DataTable.apply("name");

                for (ResourceName dataset : details.getDatasets()) {
                    dt = dt.withRow(dataset.getValue());
                }

                return CommandResult.success(dt);
            });
    }

}
