package maquette.controller.domain.api.namespaces;

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
import maquette.controller.domain.entities.namespace.Namespace;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.entities.namespace.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.namespace.protocol.commands.CreateNamespace;
import maquette.controller.domain.entities.namespace.protocol.commands.DeleteNamespace;
import maquette.controller.domain.entities.namespace.protocol.commands.GrantNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.commands.RegisterDataset;
import maquette.controller.domain.entities.namespace.protocol.commands.RemoveDataset;
import maquette.controller.domain.entities.namespace.protocol.commands.RevokeNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.namespace.protocol.events.CreatedNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.DeletedNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.GrantedNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.events.RegisteredDataset;
import maquette.controller.domain.entities.namespace.protocol.events.RemovedDataset;
import maquette.controller.domain.entities.namespace.protocol.events.RevokedNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceDetails;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceInfo;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceDetailsResult;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceInfoResult;
import maquette.controller.domain.services.CollectDatasets;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespaceDetails;
import maquette.controller.domain.values.namespace.NamespaceInfo;
import maquette.controller.domain.values.namespace.NamespacePrivilege;

@AllArgsConstructor(staticName = "apply")
public final class NamespaceContainerImpl {

    private final ActorRef<NamespacesMessage> namespacesRegistry;

    private final ActorRef<ShardingEnvelope<NamespaceMessage>> namespaces;

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

    private CompletionStage<NamespaceDetails> getNamespaceDetails() {
        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(namespace),
                    GetNamespaceDetails.apply(namespace, replyTo, errorTo)),
                GetNamespaceDetailsResult.class)
            .thenApply(GetNamespaceDetailsResult::getNamespaceDetails);
    }

    private CompletionStage<NamespaceInfo> getNamespaceInfo(ResourceName namespace) {
        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(namespace),
                    GetNamespaceInfo.apply(namespace, replyTo, errorTo)),
                GetNamespaceInfoResult.class)
            .thenApply(GetNamespaceInfoResult::getNamespaceInfo);
    }

    public CompletionStage<NamespaceInfo> changeOwner(User executor, Authorization owner) {
        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(namespace),
                    ChangeOwner.apply(namespace, executor, owner, replyTo, errorTo)),
                ChangedOwner.class)
            .thenCompose(changedOwner -> getNamespaceInfo(namespace));
    }

    public CompletionStage<DatasetDetails> createDataset(User executor, ResourceName dataset, boolean isPrivate) {
        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(namespace),
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

    public CompletionStage<NamespaceInfo> createNamespace(User executor, ResourceName name) {
        return patterns
            .ask(
                namespacesRegistry,
                (replyTo, errorTo) -> CreateNamespace.apply(name, executor, replyTo, errorTo),
                CreatedNamespace.class)
            .thenCompose(createdNamespace -> patterns.ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(name),
                    CreateNamespace.apply(name, executor, replyTo, errorTo)),
                CreatedNamespace.class))
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
                    Namespace.createEntityId(namespace),
                    RemoveDataset.apply(namespace, dataset, replyTo, errorTo)),
                RemovedDataset.class))
            .thenApply(removed -> Done.getInstance());
    }

    public CompletionStage<Done> deleteNamespace(User executor) {
        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(namespace),
                    DeleteNamespace.apply(namespace, executor, replyTo, errorTo)),
                DeletedNamespace.class)
            .thenCompose(deleted -> patterns
                .ask(
                    namespacesRegistry,
                    (replyTo, errorTo) -> DeleteNamespace.apply(namespace, executor, replyTo, errorTo),
                    DeletedNamespace.class))
            .thenApply(deleted -> Done.getInstance());
    }

    public CompletionStage<Set<DatasetDetails>> getDatasets() {
        return getNamespaceDetails()
            .thenCompose(details -> {
                NamespaceInfo info =
                    NamespaceInfo.apply(details.getName(), details.getModified(), details.getAcl(), details.getDatasets());
                Set<NamespaceInfo> infos = Sets.newHashSet(info);

                return patterns.process(result -> CollectDatasets.create(infos, datasets, result));
            });
    }

    public CompletionStage<GrantedAuthorization> grantNamespaceAccess(
        User executor, NamespacePrivilege grant, Authorization grantFor) {

        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(namespace),
                    GrantNamespaceAccess.apply(namespace, executor, grant, grantFor, replyTo, errorTo)),
                GrantedNamespaceAccess.class)
            .thenApply(GrantedNamespaceAccess::getGrantedFor);
    }

    public CompletionStage<GrantedAuthorization> revokeNamespaceAccess(
        User executor, NamespacePrivilege revoke, Authorization revokeFrom) {

        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(namespace),
                    RevokeNamespaceAccess.apply(namespace, executor, revoke, revokeFrom, replyTo, errorTo)),
                RevokedNamespaceAccess.class)
            .thenApply(RevokedNamespaceAccess::getRevokedFrom);
    }

}
