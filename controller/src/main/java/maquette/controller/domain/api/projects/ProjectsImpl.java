package maquette.controller.domain.api.projects;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.Dataset;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.protocol.results.GetDetailsResult;
import maquette.controller.domain.services.NamespaceServices;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.project.Project;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.ProjectsMessage;
import maquette.controller.domain.entities.project.protocol.queries.GetProjectDetails;
import maquette.controller.domain.entities.project.protocol.results.GetProjectDetailsResult;
import maquette.controller.domain.entities.deprecatedproject.DeprecatedProject;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectDescription;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectPrivacy;
import maquette.controller.domain.entities.deprecatedproject.protocol.commands.CreateProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.commands.DeleteProject;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectDescription;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectPrivacy;
import maquette.controller.domain.entities.deprecatedproject.protocol.events.CreatedProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.events.DeletedProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.queries.GetProjectProperties;
import maquette.controller.domain.entities.deprecatedproject.protocol.results.GetProjectPropertiesResult;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectDetails;
import maquette.controller.domain.values.project.NamespacePrivilege;
import maquette.controller.domain.values.deprecatedproject.ProjectProperties;

@AllArgsConstructor(staticName = "apply")
public final class ProjectsImpl implements Projects {

    private final ActorRef<maquette.controller.domain.entities.deprecatedproject.protocol.ProjectsMessage> projectsRegistry;

    private final ActorRef<ProjectsMessage> namespacesRegistry;

    private final ActorRef<ShardingEnvelope<maquette.controller.domain.entities.deprecatedproject.protocol.ProjectMessage>> projects;

    private final ActorRef<ShardingEnvelope<ProjectMessage>> namespaces;

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

    private final ActorPatterns patterns;

    private NamespaceServices createContainerImpl(ResourceName namespace) {
        return NamespaceServices.apply(namespacesRegistry, namespaces, datasets, patterns, namespace);
    }

    private CompletionStage<DatasetDetails> getDatasetDetails(ResourcePath dataset) {
        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(dataset),
                    GetDetails.apply(dataset, replyTo, errorTo)),
                GetDetailsResult.class)
            .thenApply(GetDetailsResult::getDetails);
    }

    private CompletionStage<ProjectDetails> getNamespaceDetails(ResourceName project) {
        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(project),
                    GetProjectDetails.apply(project, replyTo, errorTo)),
                GetProjectDetailsResult.class)
            .thenApply(GetProjectDetailsResult::getDetails);
    }

    private CompletionStage<ProjectProperties> getProjectProperties(ResourceName project) {
        return patterns
            .ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    DeprecatedProject.createEntityId(project),
                    GetProjectProperties.apply(project, replyTo, errorTo)),
                GetProjectPropertiesResult.class)
            .thenApply(GetProjectPropertiesResult::getProperties);
    }

    private CompletionStage<maquette.controller.domain.values.deprecatedproject.ProjectDetails> getProjectDetails(ResourceName project) {
        return getProjectProperties(project)
            .thenCompose(properties -> getNamespaceDetails(project)
                .thenApply(details -> maquette.controller.domain.values.deprecatedproject.ProjectDetails.apply(properties, details)));
    }

    @Override
    public CompletionStage<maquette.controller.domain.values.deprecatedproject.ProjectDetails> changeDescription(User executor, ResourceName project, Markdown description) {
        return patterns
            .ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    DeprecatedProject.createEntityId(project),
                    ChangeProjectDescription.apply(project, executor, description, replyTo, errorTo)),
                ChangedProjectDescription.class)
            .thenApply(ChangedProjectDescription::getProject)
            .thenCompose(this::getProjectDetails);
    }

    @Override
    public CompletionStage<maquette.controller.domain.values.deprecatedproject.ProjectDetails> changeOwner(User executor, ResourceName project, Authorization owner) {
        return getProjectProperties(project)
            .thenCompose(properties -> createContainerImpl(properties.getName())
                .changeOwner(executor, owner))
            .thenCompose(info -> getProjectDetails(project));
    }

    @Override
    public CompletionStage<maquette.controller.domain.values.deprecatedproject.ProjectDetails> changePrivacy(User executor, ResourceName project, boolean isPrivate) {
        return patterns
            .ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    DeprecatedProject.createEntityId(project),
                    ChangeProjectPrivacy.apply(project, executor, isPrivate, replyTo, errorTo)),
                ChangedProjectPrivacy.class)
            .thenApply(ChangedProjectPrivacy::getProject)
            .thenCompose(this::getProjectDetails);
    }

    @Override
    public CompletionStage<maquette.controller.domain.values.deprecatedproject.ProjectDetails> createProject(User executor, ResourceName project, Markdown description, boolean isPrivate) {
        return createContainerImpl(project)
            .createNamespace(executor, project)
            .thenCompose(namespaceInfo -> createProject$internal(executor, project, description, isPrivate));
    }

    private CompletionStage<maquette.controller.domain.values.deprecatedproject.ProjectDetails> createProject$internal(User executor, ResourceName project,
                                                                                                                       Markdown description, boolean isPrivate) {

        return patterns
            .ask(
                projectsRegistry,
                (replyTo, errorTo) -> CreateProject.apply(project, executor, description, isPrivate, replyTo, errorTo),
                CreatedProject.class)
            .thenCompose(createdNamespace -> patterns.ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    DeprecatedProject.createEntityId(project),
                    CreateProject.apply(project, executor, description, isPrivate, replyTo, errorTo)),
                CreatedProject.class))
            .thenCompose(createdNamespace -> getProjectDetails(project));
    }

    @Override
    public CompletionStage<DatasetDetails> createDataset(User executor, ResourcePath dataset, boolean isPrivate) {
        return getProjectDetails(dataset.getNamespace())
            .thenCompose(details -> createContainerImpl(details.getDetails().getName())
                .createDataset(executor, dataset.getName(), isPrivate)
                .thenCompose(ds -> patterns
                    .ask(
                        datasets,
                        (replyTo, errorTo) -> ShardingEnvelope.apply(
                            Dataset.createEntityId(dataset),
                            ChangeOwner.apply(
                                executor,
                                dataset,
                                details.getDetails().getAcl().getOwner().getAuthorization(),
                                replyTo,
                                errorTo)),
                        ChangedOwner.class)))
            .thenCompose(changed -> getDatasetDetails(dataset));
    }

    @Override
    public CompletionStage<Done> deleteDataset(User executor, ResourcePath dataset) {
        return getProjectProperties(dataset.getNamespace())
            .thenCompose(properties -> createContainerImpl(properties.getName()).deleteDataset(executor, dataset.getName()));
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> getDatasets(User executor, ResourceName project) {
        return getProjectProperties(project)
            .thenCompose(properties -> createContainerImpl(properties.getName()).getDatasets());
    }

    @Override
    public CompletionStage<Done> deleteProject(User executor, ResourceName project) {
        return deleteProject$internal(executor, project)
            .thenCompose(deleted -> createContainerImpl(project).deleteNamespace(executor));
    }

    private CompletionStage<Done> deleteProject$internal(User executor, ResourceName project) {
        return patterns
            .ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    DeprecatedProject.createEntityId(project),
                    DeleteProject.apply(project, executor, replyTo, errorTo)),
                DeletedProject.class)
            .thenCompose(deleted -> patterns
                .ask(
                    projectsRegistry,
                    (replyTo, errorTo) -> DeleteProject.apply(project, executor, replyTo, errorTo),
                    DeletedProject.class))
            .thenApply(deleted -> Done.getInstance());
    }

    @Override
    public CompletionStage<maquette.controller.domain.values.deprecatedproject.ProjectDetails> getDetails(User executor, ResourceName project) {
        return getProjectDetails(project);
    }

    @Override
    public CompletionStage<GrantedAuthorization> grantAccess(User executor, ResourceName project, NamespacePrivilege grant,
                                                             Authorization grantFor) {
        return getProjectProperties(project)
            .thenCompose(properties -> createContainerImpl(properties.getName()).grantNamespaceAccess(executor, grant, grantFor));
    }

    @Override
    public CompletionStage<GrantedAuthorization> revokeNamespaceAccess(User executor, ResourceName project,
                                                                       NamespacePrivilege revoke, Authorization revokeFrom) {
        return getProjectProperties(project)
            .thenCompose(properties -> createContainerImpl(properties.getName()).revokeNamespaceAccess(
                executor,
                revoke,
                revokeFrom));
    }

}
