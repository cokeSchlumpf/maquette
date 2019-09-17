package maquette.controller.adapters.cli.commands.datasets;

import java.util.Set;
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
        ResourceName resource = ResourceName.apply(executor, namespace);

        return app
            .namespaces()
            .getNamespaceDetails(executor, resource)
            .thenApply(details -> {
                DataTable dt = DataTable.apply("name");

                Set<ResourceName> datasets = details.getDatasets();
                for (ResourceName dataset : datasets) {
                    dt = dt.withRow(dataset);
                }

                return CommandResult.success(dt);
            });
    }

}
