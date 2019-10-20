package maquette.controller.domain.services;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import com.google.common.collect.Sets;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.Dataset;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.DeleteDataset;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.DeletedDataset;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.protocol.results.GetDetailsResult;
import maquette.controller.domain.entities.project.Project;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.ProjectsMessage;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectOwner;
import maquette.controller.domain.entities.project.protocol.commands.CreateProject;
import maquette.controller.domain.entities.project.protocol.commands.DeleteProject;
import maquette.controller.domain.entities.project.protocol.commands.GrantProjectAccess;
import maquette.controller.domain.entities.project.protocol.commands.RegisterDataset;
import maquette.controller.domain.entities.project.protocol.commands.RemoveDataset;
import maquette.controller.domain.entities.project.protocol.commands.RevokeProjectAccess;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectOwner;
import maquette.controller.domain.entities.project.protocol.events.CreatedProject;
import maquette.controller.domain.entities.project.protocol.events.DeletedProject;
import maquette.controller.domain.entities.project.protocol.events.GrantedProjectAccess;
import maquette.controller.domain.entities.project.protocol.events.RegisteredDataset;
import maquette.controller.domain.entities.project.protocol.events.RemovedDataset;
import maquette.controller.domain.entities.project.protocol.events.RevokedProjectAccess;
import maquette.controller.domain.entities.project.protocol.queries.GetProjectDetails;
import maquette.controller.domain.entities.project.protocol.queries.GetProjectInfo;
import maquette.controller.domain.entities.project.protocol.results.GetProjectDetailsResult;
import maquette.controller.domain.entities.project.protocol.results.GetProjectInfoResult;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectDetails;
import maquette.controller.domain.values.project.ProjectInfo;
import maquette.controller.domain.values.project.NamespacePrivilege;

@AllArgsConstructor(staticName = "apply")
public final class NamespaceServices {

    private final ActorRef<ProjectsMessage> namespacesRegistry;

    private final ActorRef<ShardingEnvelope<ProjectMessage>> namespaces;

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

    private final ActorPatterns patterns;

    private final ResourceName namespace;

    private CompletionStage<DatasetDetails> getDatasetDetails(ResourceName dataset) {
        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(ResourcePath.apply(namespace, dataset)),
                    GetDetails.apply(ResourcePath.apply(namespace, dataset), replyTo, errorTo)),
                GetDetailsResult.class)
            .thenApply(GetDetailsResult::getDetails);
    }

    private CompletionStage<ProjectDetails> getNamespaceDetails() {
        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(namespace),
                    GetProjectDetails.apply(namespace, replyTo, errorTo)),
                GetProjectDetailsResult.class)
            .thenApply(GetProjectDetailsResult::getDetails);
    }

    private CompletionStage<ProjectInfo> getNamespaceInfo(ResourceName namespace) {
        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(namespace),
                    GetProjectInfo.apply(namespace, replyTo, errorTo)),
                GetProjectInfoResult.class)
            .thenApply(GetProjectInfoResult::getInfo);
    }

    public CompletionStage<ProjectInfo> changeOwner(User executor, Authorization owner) {
        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(namespace),
                    ChangeProjectOwner.apply(namespace, executor, owner, replyTo, errorTo)),
                ChangedProjectOwner.class)
            .thenCompose(changedOwner -> getNamespaceInfo(namespace));
    }

    public CompletionStage<DatasetDetails> createDataset(User executor, ResourceName dataset, boolean isPrivate) {
        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(namespace),
                    RegisterDataset.apply(namespace, dataset, replyTo, errorTo)),
                RegisteredDataset.class)
            .thenCompose(result -> patterns.ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(ResourcePath.apply(namespace, dataset)),
                    CreateDataset.apply(ResourcePath.apply(namespace, dataset), executor, isPrivate, replyTo, errorTo)),
                CreatedDataset.class))
            .thenCompose(result -> getDatasetDetails(dataset));
    }

    public CompletionStage<ProjectInfo> createNamespace(User executor, ResourceName name) {
        return patterns
            .ask(
                namespacesRegistry,
                (replyTo, errorTo) -> CreateProject.apply(name, executor, replyTo, errorTo),
                CreatedProject.class)
            .thenCompose(createdNamespace -> patterns.ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(name),
                    CreateProject.apply(name, executor, replyTo, errorTo)),
                CreatedProject.class))
            .thenCompose(createdNamespace -> getNamespaceInfo(name));
    }

    public CompletionStage<Done> deleteDataset(User executor, ResourceName dataset) {
        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(ResourcePath.apply(namespace, dataset)),
                    DeleteDataset.apply(ResourcePath.apply(namespace, dataset), executor, replyTo, errorTo)),
                DeletedDataset.class)
            .thenCompose(deleted -> patterns.ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(namespace),
                    RemoveDataset.apply(namespace, dataset, replyTo, errorTo)),
                RemovedDataset.class))
            .thenApply(removed -> Done.getInstance());
    }

    public CompletionStage<Done> deleteNamespace(User executor) {
        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(namespace),
                    DeleteProject.apply(namespace, executor, replyTo, errorTo)),
                DeletedProject.class)
            .thenCompose(deleted -> patterns
                .ask(
                    namespacesRegistry,
                    (replyTo, errorTo) -> DeleteProject.apply(namespace, executor, replyTo, errorTo),
                    DeletedProject.class))
            .thenApply(deleted -> Done.getInstance());
    }

    public CompletionStage<Set<DatasetDetails>> getDatasets() {
        return getNamespaceDetails()
            .thenCompose(details -> {
                ProjectInfo info =
                    ProjectInfo.apply(details.getName(), details.getModified(), details.getAcl(), details.getDatasets());
                Set<ProjectInfo> infos = Sets.newHashSet(info);

                return patterns.process(result -> CollectDatasets.create(infos, datasets, result));
            });
    }

    public CompletionStage<GrantedAuthorization> grantNamespaceAccess(
        User executor, NamespacePrivilege grant, Authorization grantFor) {

        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(namespace),
                    GrantProjectAccess.apply(namespace, executor, grant, grantFor, replyTo, errorTo)),
                GrantedProjectAccess.class)
            .thenApply(GrantedProjectAccess::getGrantedFor);
    }

    public CompletionStage<GrantedAuthorization> revokeNamespaceAccess(
        User executor, NamespacePrivilege revoke, Authorization revokeFrom) {

        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(namespace),
                    RevokeProjectAccess.apply(namespace, executor, revoke, revokeFrom, replyTo, errorTo)),
                RevokedProjectAccess.class)
            .thenApply(RevokedProjectAccess::getRevokedFrom);
    }

}
