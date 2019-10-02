package maquette.controller.domain.api.projects;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.Dataset;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.protocol.results.GetDetailsResult;
import maquette.controller.domain.entities.namespace.Namespace;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceDetails;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceDetailsResult;
import maquette.controller.domain.entities.project.Project;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.queries.GetProjectProperties;
import maquette.controller.domain.entities.project.protocol.results.GetProjectPropertiesResult;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.exceptions.NotAuthorizedException;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespaceDetails;
import maquette.controller.domain.values.namespace.NamespacePrivilege;
import maquette.controller.domain.values.project.ProjectDetails;
import maquette.controller.domain.values.project.ProjectProperties;

@AllArgsConstructor(staticName = "apply")
public final class ProjectsSecured implements Projects {

    private final ActorRef<ShardingEnvelope<ProjectMessage>> projects;

    private final ActorRef<ShardingEnvelope<NamespaceMessage>> namespaces;

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

    private final ActorPatterns patterns;

    private final Projects delegate;

    private CompletionStage<DatasetDetails> getDatasetDetails(ResourceName namespace, ResourceName dataset) {
        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(ResourcePath.apply(namespace, dataset)),
                    GetDetails.apply(ResourcePath.apply(namespace, dataset), replyTo, errorTo)),
                GetDetailsResult.class)
            .thenApply(GetDetailsResult::getDetails);
    }

    private CompletionStage<NamespaceDetails> getNamespaceDetails(ResourceName namespace) {
        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) ->
                    ShardingEnvelope.apply(
                        Namespace.createEntityId(namespace),
                        GetNamespaceDetails.apply(namespace, replyTo, errorTo)),
                GetNamespaceDetailsResult.class)
            .thenApply(GetNamespaceDetailsResult::getNamespaceDetails);
    }

    private CompletionStage<ProjectProperties> getProjectProperties(ResourceName project) {
        return patterns
            .ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(project),
                    GetProjectProperties.apply(project, replyTo, errorTo)),
                GetProjectPropertiesResult.class)
            .thenApply(GetProjectPropertiesResult::getProperties);
    }

    private CompletionStage<ProjectDetails> getProjectDetails(ResourceName project) {
        return getProjectProperties(project)
            .thenCompose(properties -> getNamespaceDetails(project)
                .thenApply(details -> ProjectDetails.apply(properties, details)));
    }

    @Override
    public CompletionStage<ProjectDetails> changeDescription(User executor, ResourceName project, Markdown description) {
        return getProjectDetails(project)
            .thenApply(ProjectDetails::getDetails)
            .thenCompose(nsDetails -> {
                if (nsDetails.getAcl().canManage(executor)) {
                    return delegate.changeDescription(executor, project, description);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<ProjectDetails> changeOwner(User executor, ResourceName project, Authorization owner) {
        return getProjectDetails(project)
            .thenApply(ProjectDetails::getDetails)
            .thenCompose(nsDetails -> {
                if (nsDetails.getAcl().canManage(executor)) {
                    return delegate.changeOwner(executor, project, owner);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<ProjectDetails> changePrivacy(User executor, ResourceName project, boolean isPrivate) {
        return getProjectDetails(project)
            .thenApply(ProjectDetails::getDetails)
            .thenCompose(nsDetails -> {
                if (nsDetails.getAcl().canManage(executor)) {
                    return delegate.changePrivacy(executor, project, isPrivate);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<DatasetDetails> createDataset(User executor, ResourcePath dataset, boolean isPrivate) {
        return getProjectDetails(dataset.getNamespace())
            .thenApply(ProjectDetails::getDetails)
            .thenCompose(nsDetails -> {
                if (nsDetails.getAcl().canCreatedDataset(executor)) {
                    return delegate.createDataset(executor, dataset, isPrivate);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            })
            .thenCompose(dsDetails -> getNamespaceDetails(dataset.getNamespace()))
            .thenCompose(nsDetails -> {
                Authorization owner = nsDetails.getAcl().getOwner().getAuthorization();
                return delegate.changeOwner(executor, dataset.getNamespace(), owner);
            })
            .thenCompose(prDetails -> getDatasetDetails(dataset.getNamespace(), dataset.getName()));
    }

    @Override
    public CompletionStage<Done> deleteDataset(User executor, ResourcePath dataset) {
        return getProjectDetails(dataset.getNamespace())
            .thenApply(ProjectDetails::getDetails)
            .thenCompose(details -> {
                if (details.getAcl().canDeleteNamespace(executor)) {
                    return delegate.deleteDataset(executor, dataset);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> getDatasets(User executor, ResourceName project) {
        return getProjectDetails(project)
            .thenApply(details -> {
                if (!details.getProperties().isPrivate()) {
                    return true;
                } else {
                    return details.getDetails().getAcl().canReadDetails(executor);
                }
            })
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.getDatasets(executor, project);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<ProjectDetails> createProject(User executor, ResourceName project, ProjectProperties properties) {
        if (executor instanceof AuthenticatedUser) {
            return delegate.createProject(executor, project, properties);
        } else {
            return CompletableFuture.supplyAsync(() -> {
                throw NotAuthorizedException.apply(executor);
            });
        }
    }

    @Override
    public CompletionStage<Done> deleteProject(User executor, ResourceName project) {
        return getProjectDetails(project)
            .thenApply(ProjectDetails::getDetails)
            .thenCompose(details -> {
                if (details.getAcl().canManage(executor)) {
                    return delegate.deleteProject(executor, project);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<ProjectDetails> getDetails(User executor, ResourceName project) {
        return getProjectDetails(project)
            .thenApply(ProjectDetails::getDetails)
            .thenCompose(details -> {
                if (details.getAcl().canReadDetails(executor)) {
                    return delegate.getDetails(executor, project);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<GrantedAuthorization> grantAccess(User executor, ResourceName project, NamespacePrivilege grant,
                                                             Authorization grantFor) {
        return getProjectDetails(project)
            .thenApply(ProjectDetails::getDetails)
            .thenCompose(details -> {
                if (details.getAcl().canManage(executor)) {
                    return delegate.grantAccess(executor, project, grant, grantFor);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<GrantedAuthorization> revokeNamespaceAccess(User executor, ResourceName project,
                                                                       NamespacePrivilege revoke, Authorization revokeFrom) {
        return getProjectDetails(project)
            .thenApply(ProjectDetails::getDetails)
            .thenCompose(details -> {
                if (details.getAcl().canManage(executor)) {
                    return delegate.revokeNamespaceAccess(executor, project, revoke, revokeFrom);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

}
