package maquette.controller.domain.acl.projects;

import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.controller.domain.services.CreateDefaultProject;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectDetails;
import maquette.controller.domain.values.project.ProjectPrivilege;

@AllArgsConstructor(staticName = "apply")
public final class ProjectsUserActivity implements Projects {

    private final Projects delegate;

    private final CreateDefaultProject createDefaultProject;

    private <T> CompletionStage<T> createDefaultNamespace(User executor, Function<Projects, CompletionStage<T>> andThen) {
        return createDefaultProject.run(executor, () -> andThen.apply(delegate));
    }

    @Override
    public CompletionStage<ProjectDetails> changeDescription(User executor, ResourceName project, Markdown description) {
        return createDefaultNamespace(executor, p -> p.changeDescription(executor, project, description));
    }

    @Override
    public CompletionStage<ProjectDetails> changeOwner(User executor, ResourceName project, Authorization owner) {
        return createDefaultNamespace(executor, p -> p.changeOwner(executor, project, owner));
    }

    @Override
    public CompletionStage<ProjectDetails> changePrivacy(User executor, ResourceName project, boolean isPrivate) {
        return createDefaultNamespace(executor, p -> p.changePrivacy(executor, project, isPrivate));
    }

    @Override
    public CompletionStage<ProjectDetails> create(User executor, ResourceName project, Markdown description, boolean isPrivate) {
        return createDefaultNamespace(executor, p -> p.create(executor, project, description, isPrivate));
    }

    @Override
    public CompletionStage<Done> delete(User executor, ResourceName project) {
        return createDefaultNamespace(executor, p -> p.delete(executor, project));
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> getDatasets(User executor, ResourceName project) {
        return createDefaultNamespace(executor, p -> p.getDatasets(executor, project));
    }

    @Override
    public CompletionStage<ProjectDetails> getDetails(User executor, ResourceName project) {
        return createDefaultNamespace(executor, p -> p.getDetails(executor, project));
    }

    @Override
    public CompletionStage<GrantedAuthorization> grantAccess(User executor, ResourceName project, ProjectPrivilege grant,
                                                             Authorization grantFor) {
        return createDefaultNamespace(executor, p -> p.grantAccess(executor, project, grant, grantFor));
    }

    @Override
    public CompletionStage<GrantedAuthorization> revokeAccess(User executor, ResourceName project,
                                                              ProjectPrivilege revoke, Authorization revokeFrom) {
        return createDefaultNamespace(executor, p -> p.revokeAccess(executor, project, revoke, revokeFrom));
    }

}
