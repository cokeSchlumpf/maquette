package maquette.controller.domain.api.projects;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import akka.Done;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespacePrivilege;
import maquette.controller.domain.values.project.ProjectDetails;
import maquette.controller.domain.values.project.ProjectProperties;

public interface Projects {

    CompletionStage<ProjectDetails> changeDescription(User executor, ResourceName project, Markdown description);

    CompletionStage<ProjectDetails> changeOwner(User executor, ResourceName project, Authorization owner);

    CompletionStage<ProjectDetails> changePrivacy(User executor, ResourceName project, boolean isPrivate);

    CompletionStage<DatasetDetails> createDataset(User executor, ResourcePath name, boolean isPrivate);

    CompletionStage<ProjectDetails> createProject(User executor, ResourceName project, Markdown description, boolean isPrivate);

    CompletionStage<Done> deleteDataset(User executor, ResourcePath dataset);

    CompletionStage<Done> deleteProject(User executor, ResourceName project);

    CompletionStage<Set<DatasetDetails>> getDatasets(User executor, ResourceName project);

    CompletionStage<ProjectDetails> getDetails(User executor, ResourceName project);

    CompletionStage<GrantedAuthorization> grantAccess(
        User executor, ResourceName project, NamespacePrivilege grant, Authorization grantFor);

    CompletionStage<GrantedAuthorization> revokeNamespaceAccess(
        User executor, ResourceName project, NamespacePrivilege revoke, Authorization revokeFrom);

}
