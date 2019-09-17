package maquette.controller.adapters.cli.commands;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.commands.datasets.CreateDatasetCmd;
import maquette.controller.adapters.cli.commands.datasets.GrantDatasetAccessCmd;
import maquette.controller.adapters.cli.commands.datasets.ListDatasetVersionsCmd;
import maquette.controller.adapters.cli.commands.datasets.ListDatasetsCmd;
import maquette.controller.adapters.cli.commands.datasets.PrintDatasetDetailsCmd;
import maquette.controller.adapters.cli.commands.datasets.PrintDatasetVersionDetailsCmd;
import maquette.controller.adapters.cli.commands.datasets.RevokeDatasetAccessCmd;
import maquette.controller.adapters.cli.commands.namespaces.CreateNamespaceCmd;
import maquette.controller.adapters.cli.commands.namespaces.GrantNamespaceAccessCmd;
import maquette.controller.adapters.cli.commands.namespaces.ListNamespacesCmd;
import maquette.controller.adapters.cli.commands.namespaces.PrintNamespaceDetailsCmd;
import maquette.controller.adapters.cli.commands.namespaces.RevokeNamespaceAccessCmd;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.iam.User;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "command")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = CreateDatasetCmd.class, name = "datasets create"),
        @JsonSubTypes.Type(value = GrantDatasetAccessCmd.class, name = "dataset grant"),
        @JsonSubTypes.Type(value = ListDatasetsCmd.class, name = "datasets"),
        @JsonSubTypes.Type(value = ListDatasetVersionsCmd.class, name = "dataset versions"),
        @JsonSubTypes.Type(value = PrintDatasetDetailsCmd.class, name = "dataset show"),
        @JsonSubTypes.Type(value = PrintDatasetVersionDetailsCmd.class, name = "dataset version show"),
        @JsonSubTypes.Type(value = RevokeDatasetAccessCmd.class, name = "dataset revoke"),

        @JsonSubTypes.Type(value = CreateNamespaceCmd.class, name = "namespaces create"),
        @JsonSubTypes.Type(value = GrantNamespaceAccessCmd.class, name = "namespace grant"),
        @JsonSubTypes.Type(value = ListNamespacesCmd.class, name = "namespaces"),
        @JsonSubTypes.Type(value = PrintNamespaceDetailsCmd.class, name = "namespace show"),
        @JsonSubTypes.Type(value = RevokeNamespaceAccessCmd.class, name = "namespace revoke")
    })
public interface Command {

    CompletionStage<CommandResult> run(User executor, CoreApplication app);

}

