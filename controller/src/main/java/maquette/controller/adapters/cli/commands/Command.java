package maquette.controller.adapters.cli.commands;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.commands.datasets.ChangeDatasetDescriptionCmd;
import maquette.controller.adapters.cli.commands.datasets.ChangeDatasetPrivacyCmd;
import maquette.controller.adapters.cli.commands.datasets.CreateDatasetConsumerToken;
import maquette.controller.adapters.cli.commands.datasets.CreateDatasetProducerToken;
import maquette.controller.adapters.cli.commands.datasets.DeleteDatasetCmd;
import maquette.controller.adapters.cli.commands.datasets.GrantDatasetAccessCmd;
import maquette.controller.adapters.cli.commands.datasets.ListDatasetVersionsCmd;
import maquette.controller.adapters.cli.commands.datasets.PrintDatasetDetailsCmd;
import maquette.controller.adapters.cli.commands.datasets.PrintDatasetVersionDetailsCmd;
import maquette.controller.adapters.cli.commands.datasets.RequestDatasetAccessCmd;
import maquette.controller.adapters.cli.commands.datasets.RevokeDatasetAccessCmd;
import maquette.controller.adapters.cli.commands.projects.ChangeProjectDescriptionCmd;
import maquette.controller.adapters.cli.commands.projects.ChangeProjectOwnerCmd;
import maquette.controller.adapters.cli.commands.projects.ChangeProjectPrivacyCmd;
import maquette.controller.adapters.cli.commands.projects.CreateProjectCmd;
import maquette.controller.adapters.cli.commands.projects.GrantProjectAccessCmd;
import maquette.controller.adapters.cli.commands.projects.ListProjectDatasetsCmd;
import maquette.controller.adapters.cli.commands.projects.PrintProjectDetailsCmd;
import maquette.controller.adapters.cli.commands.projects.RevokeProjectAccessCmd;
import maquette.controller.adapters.cli.commands.shop.FindProjectsCmd;
import maquette.controller.adapters.cli.commands.shop.ListDatasetsCmd;
import maquette.controller.adapters.cli.commands.shop.ListProjectsCmd;
import maquette.controller.adapters.cli.commands.users.DeleteTokenCmd;
import maquette.controller.adapters.cli.commands.users.ListTokensCmd;
import maquette.controller.adapters.cli.commands.users.RegisterTokenCmd;
import maquette.controller.adapters.cli.commands.users.RenewTokenCmd;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.iam.User;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "command")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = ChangeDatasetDescriptionCmd.class, name = "dataset change description"),
        @JsonSubTypes.Type(value = ChangeDatasetPrivacyCmd.class, name = "dataset change privacy"),
        @JsonSubTypes.Type(value = CreateDatasetConsumerToken.class, name = "dataset create consumer"),
        @JsonSubTypes.Type(value = CreateProjectCmd.class, name = "dataset create"),
        @JsonSubTypes.Type(value = CreateDatasetProducerToken.class, name = "dataset create producer"),
        @JsonSubTypes.Type(value = DeleteDatasetCmd.class, name = "dataset delete"),
        @JsonSubTypes.Type(value = GrantDatasetAccessCmd.class, name = "dataset grant"),
        @JsonSubTypes.Type(value = ListDatasetVersionsCmd.class, name = "dataset versions"),
        @JsonSubTypes.Type(value = PrintDatasetDetailsCmd.class, name = "dataset show"),
        @JsonSubTypes.Type(value = PrintDatasetVersionDetailsCmd.class, name = "dataset version show"),
        @JsonSubTypes.Type(value = RequestDatasetAccessCmd.class, name = "datasets request access"),
        @JsonSubTypes.Type(value = RevokeDatasetAccessCmd.class, name = "dataset revoke"),

        @JsonSubTypes.Type(value = ChangeProjectDescriptionCmd.class, name = "project change description"),
        @JsonSubTypes.Type(value = ChangeProjectOwnerCmd.class, name = "project change owner"),
        @JsonSubTypes.Type(value = ChangeProjectPrivacyCmd.class, name = "project change privacy"),
        @JsonSubTypes.Type(value = GrantProjectAccessCmd.class, name = "project grant"),
        @JsonSubTypes.Type(value = ListProjectDatasetsCmd.class, name = "project datasets"),
        @JsonSubTypes.Type(value = PrintProjectDetailsCmd.class, name = "project show"),
        @JsonSubTypes.Type(value = RevokeProjectAccessCmd.class, name = "project revoke"),

        @JsonSubTypes.Type(value = FindProjectsCmd.class, name = "shop projects find"),
        @JsonSubTypes.Type(value = ListProjectsCmd.class, name = "shop projects list"),
        @JsonSubTypes.Type(value = ListDatasetsCmd.class, name = "shop datasets list"),

        @JsonSubTypes.Type(value = DeleteTokenCmd.class, name = "user token delete"),
        @JsonSubTypes.Type(value = ListTokensCmd.class, name = "user tokens"),
        @JsonSubTypes.Type(value = RegisterTokenCmd.class, name = "user token register"),
        @JsonSubTypes.Type(value = RenewTokenCmd.class, name = "user token renew")
    })
public interface Command {

    CompletionStage<CommandResult> run(User executor, CoreApplication app);

}

