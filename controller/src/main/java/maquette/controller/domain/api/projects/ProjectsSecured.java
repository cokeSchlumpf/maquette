package maquette.controller.domain.api.projects;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.project.Project;
import maquette.controller.domain.entities.project.protocol.queries.GetProjectDetails;
import maquette.controller.domain.entities.project.protocol.results.GetProjectDetailsResult;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.exceptions.NotAuthorizedException;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectDetails;
import maquette.controller.domain.values.project.ProjectPrivilege;

@AllArgsConstructor(staticName = "apply")
public final class ProjectsSecured implements Projects {

    private final ActorRef<ShardingEnvelope<maquette.controller.domain.entities.project.protocol.ProjectMessage>> projects;

    private final ActorPatterns patterns;

    private final Projects delegate;

    private CompletionStage<ProjectDetails> getProjectDetails(ResourceName project) {
        return patterns
            .ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(project),
                    GetProjectDetails.apply(project, replyTo, errorTo)),
                GetProjectDetailsResult.class)
            .thenApply(GetProjectDetailsResult::getDetails);
    }

    @Override
    public CompletionStage<ProjectDetails> changeDescription(User executor, ResourceName project, Markdown description) {
        return getProjectDetails(project)
            .thenCompose(details -> {
                if (details.getAcl().canManage(executor)) {
                    return delegate.changeDescription(executor, project, description);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<ProjectDetails> changeOwner(User executor, ResourceName project, Authorization owner) {
        return getProjectDetails(project)
            .thenCompose(details -> {
                if (details.getAcl().canManage(executor)) {
                    return delegate.changeOwner(executor, project, owner);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<ProjectDetails> changePrivacy(User executor, ResourceName project, boolean isPrivate) {
        return getProjectDetails(project)
            .thenCompose(details -> {
                if (details.getAcl().canManage(executor)) {
                    return delegate.changePrivacy(executor, project, isPrivate);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> getDatasets(User executor, ResourceName project) {
        return getProjectDetails(project)
            .thenApply(details -> details.getAcl().canFind(executor))
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.getDatasets(executor, project);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            })
            .thenApply(datasets -> datasets
                .stream()
                .filter(details -> details.getAcl().canFind(executor))
                .collect(Collectors.toSet()));
    }

    @Override
    public CompletionStage<ProjectDetails> create(User executor, ResourceName project, Markdown description, boolean isPrivate) {
        if (executor instanceof AuthenticatedUser) {
            return delegate.create(executor, project, description, isPrivate);
        } else {
            return CompletableFuture.supplyAsync(() -> {
                throw NotAuthorizedException.apply(executor);
            });
        }
    }

    @Override
    public CompletionStage<Done> delete(User executor, ResourceName project) {
        return getProjectDetails(project)
            .thenCompose(details -> {
                if (details.getAcl().canManage(executor)) {
                    return delegate.delete(executor, project);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<ProjectDetails> getDetails(User executor, ResourceName project) {
        return getProjectDetails(project)
            .thenCompose(details -> {
                if (details.getAcl().canFind(executor)) {
                    return delegate.getDetails(executor, project);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<GrantedAuthorization> grantAccess(User executor, ResourceName project, ProjectPrivilege grant,
                                                             Authorization grantFor) {
        return getProjectDetails(project)
            .thenCompose(details -> {
                if (details.getAcl().canManage(executor)) {
                    return delegate.grantAccess(executor, project, grant, grantFor);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<GrantedAuthorization> revokeAccess(User executor, ResourceName project,
                                                              ProjectPrivilege revoke, Authorization revokeFrom) {
        return getProjectDetails(project)
            .thenCompose(details -> {
                if (details.getAcl().canManage(executor)) {
                    return delegate.revokeAccess(executor, project, revoke, revokeFrom);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

}
