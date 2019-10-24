package maquette.controller.application.commands.commands;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import maquette.controller.application.commands.CommandResult;
import maquette.controller.application.commands.OutputFormat;
import maquette.controller.application.commands.commands.datasets.ApproveDatasetAccessRequestCmd;
import maquette.controller.application.commands.commands.datasets.ChangeDatasetDescriptionCmd;
import maquette.controller.application.commands.commands.datasets.ChangeDatasetPrivacyCmd;
import maquette.controller.application.commands.commands.datasets.CreateDatasetConsumerToken;
import maquette.controller.application.commands.commands.datasets.CreateDatasetProducerToken;
import maquette.controller.application.commands.commands.datasets.DeleteDatasetCmd;
import maquette.controller.application.commands.commands.datasets.GrantDatasetAccessCmd;
import maquette.controller.application.commands.commands.datasets.ListDatasetVersionsCmd;
import maquette.controller.application.commands.commands.datasets.PrintDatasetDetailsCmd;
import maquette.controller.application.commands.commands.datasets.PrintDatasetVersionDetailsCmd;
import maquette.controller.application.commands.commands.datasets.RequestDatasetAccessCmd;
import maquette.controller.application.commands.commands.datasets.RevokeDatasetAccessCmd;
import maquette.controller.application.commands.commands.projects.ChangeProjectDescriptionCmd;
import maquette.controller.application.commands.commands.projects.ChangeProjectOwnerCmd;
import maquette.controller.application.commands.commands.projects.ChangeProjectPrivacyCmd;
import maquette.controller.application.commands.commands.projects.CreateProjectCmd;
import maquette.controller.application.commands.commands.projects.GrantProjectAccessCmd;
import maquette.controller.application.commands.commands.projects.ListProjectDatasetsCmd;
import maquette.controller.application.commands.commands.projects.PrintProjectDetailsCmd;
import maquette.controller.application.commands.commands.projects.RevokeProjectAccessCmd;
import maquette.controller.application.commands.commands.shop.FindDatasetsCmd;
import maquette.controller.application.commands.commands.shop.FindProjectsCmd;
import maquette.controller.application.commands.commands.shop.ListDatasetsCmd;
import maquette.controller.application.commands.commands.shop.ListProjectsCmd;
import maquette.controller.application.commands.commands.users.DeleteTokenCmd;
import maquette.controller.application.commands.commands.users.GetNotificationsCmd;
import maquette.controller.application.commands.commands.users.ListTokensCmd;
import maquette.controller.application.commands.commands.users.RegisterTokenCmd;
import maquette.controller.application.commands.commands.users.RenewTokenCmd;
import maquette.controller.application.commands.commands.users.ShowUserCmd;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.entities.notifcation.protocol.commands.MarkNotificationAsRead;
import maquette.controller.domain.values.iam.User;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "command")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = AboutCmd.class, name = "about"),

        @JsonSubTypes.Type(value = ApproveDatasetAccessRequestCmd.class, name = "dataset approve access request"),
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

        @JsonSubTypes.Type(value = FindDatasetsCmd.class, name = "shop find datasets"),
        @JsonSubTypes.Type(value = FindProjectsCmd.class, name = "shop find projects"),
        @JsonSubTypes.Type(value = ListDatasetsCmd.class, name = "shop list datasets"),
        @JsonSubTypes.Type(value = ListProjectsCmd.class, name = "shop list projects"),

        @JsonSubTypes.Type(value = DeleteTokenCmd.class, name = "user token delete"),
        @JsonSubTypes.Type(value = GetNotificationsCmd.class, name = "user get notifications"),
        @JsonSubTypes.Type(value = ListTokensCmd.class, name = "user tokens"),
        @JsonSubTypes.Type(value = MarkNotificationAsRead.class, name = "user mark notification read"),
        @JsonSubTypes.Type(value = RegisterTokenCmd.class, name = "user token register"),
        @JsonSubTypes.Type(value = RenewTokenCmd.class, name = "user token renew"),
        @JsonSubTypes.Type(value = ShowUserCmd.class, name = "user show")
    })
public interface Command {

    CompletionStage<CommandResult> run(User executor, CoreApplication app, OutputFormat outputFormat);

}

