package maquette.controller.adapters.cli.commands;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.commands.datasets.CreateDatasetCmd;
import maquette.controller.adapters.cli.commands.datasets.CreateDatasetConsumerToken;
import maquette.controller.adapters.cli.commands.datasets.CreateDatasetProducerToken;
import maquette.controller.adapters.cli.commands.datasets.GrantDatasetAccessCmd;
import maquette.controller.adapters.cli.commands.datasets.ListDatasetVersionsCmd;
import maquette.controller.adapters.cli.commands.datasets.ListDatasetsCmd;
import maquette.controller.adapters.cli.commands.datasets.ListNamespaceDatasetsCmd;
import maquette.controller.adapters.cli.commands.datasets.PrintDatasetDetailsCmd;
import maquette.controller.adapters.cli.commands.datasets.PrintDatasetVersionDetailsCmd;
import maquette.controller.adapters.cli.commands.datasets.RevokeDatasetAccessCmd;
import maquette.controller.adapters.cli.commands.namespaces.CreateNamespaceCmd;
import maquette.controller.adapters.cli.commands.namespaces.GrantNamespaceAccessCmd;
import maquette.controller.adapters.cli.commands.namespaces.ListNamespacesCmd;
import maquette.controller.adapters.cli.commands.namespaces.PrintNamespaceDetailsCmd;
import maquette.controller.adapters.cli.commands.namespaces.RevokeNamespaceAccessCmd;
import maquette.controller.adapters.cli.commands.users.DeleteTokenCmd;
import maquette.controller.adapters.cli.commands.users.ListTokensCmd;
import maquette.controller.adapters.cli.commands.users.RegisterTokenCmd;
import maquette.controller.adapters.cli.commands.users.RenewTokenCmd;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.iam.User;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "command")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = CreateDatasetCmd.class, name = "datasets create"),
        @JsonSubTypes.Type(value = CreateDatasetConsumerToken.class, name = "dataset create consumer"),
        @JsonSubTypes.Type(value = CreateDatasetProducerToken.class, name = "dataset create producer"),
        @JsonSubTypes.Type(value = GrantDatasetAccessCmd.class, name = "dataset grant"),
        @JsonSubTypes.Type(value = ListDatasetsCmd.class, name = "datasets"),
        @JsonSubTypes.Type(value = ListDatasetVersionsCmd.class, name = "dataset versions"),
        @JsonSubTypes.Type(value = PrintDatasetDetailsCmd.class, name = "dataset show"),
        @JsonSubTypes.Type(value = PrintDatasetVersionDetailsCmd.class, name = "dataset version show"),
        @JsonSubTypes.Type(value = RevokeDatasetAccessCmd.class, name = "dataset revoke"),

        @JsonSubTypes.Type(value = CreateNamespaceCmd.class, name = "namespaces create"),
        @JsonSubTypes.Type(value = GrantNamespaceAccessCmd.class, name = "namespace grant"),
        @JsonSubTypes.Type(value = ListNamespacesCmd.class, name = "namespaces"),
        @JsonSubTypes.Type(value = ListNamespaceDatasetsCmd.class, name = "namespace datasets"),
        @JsonSubTypes.Type(value = PrintNamespaceDetailsCmd.class, name = "namespace show"),
        @JsonSubTypes.Type(value = RevokeNamespaceAccessCmd.class, name = "namespace revoke"),

        @JsonSubTypes.Type(value = DeleteTokenCmd.class, name = "user token delete"),
        @JsonSubTypes.Type(value = ListTokensCmd.class, name = "user tokens"),
        @JsonSubTypes.Type(value = RegisterTokenCmd.class, name = "user token register"),
        @JsonSubTypes.Type(value = RenewTokenCmd.class, name = "user token renew")
    })
public interface Command {

    CompletionStage<CommandResult> run(User executor, CoreApplication app);

}

