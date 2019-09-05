package maquette.controller.domain.api.namespaces;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.namespace.Namespace;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.entities.namespace.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.namespace.protocol.commands.CreateNamespace;
import maquette.controller.domain.entities.namespace.protocol.commands.DeleteNamespace;
import maquette.controller.domain.entities.namespace.protocol.commands.GrantNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.commands.RevokeNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.namespace.protocol.events.CreatedNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.DeletedNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.GrantedNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.events.RevokedNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceDetails;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceInfo;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceDetailsResult;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceInfoResult;
import maquette.controller.domain.services.CollectNamespaceInfos;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespaceDetails;
import maquette.controller.domain.values.namespace.NamespaceInfo;
import maquette.controller.domain.values.namespace.NamespacePrivilege;

@AllArgsConstructor(staticName = "apply")
public final class NamespacesImpl implements Namespaces {

    private final ActorRef<NamespacesMessage> namespaces;

    private final ActorRef<ShardingEnvelope<NamespaceMessage>> shards;

    private final ActorPatterns patterns;

    @Override
    public CompletionStage<maquette.controller.domain.values.namespace.NamespaceInfo> createNamespace(User executor,
                                                                                                      ResourceName name) {
        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> CreateNamespace.apply(name, executor, replyTo, errorTo),
                CreatedNamespace.class)
            .thenCompose(createdNamespace -> patterns.ask(
                shards,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(name),
                    CreateNamespace.apply(name, executor, replyTo, errorTo)),
                CreatedNamespace.class))
            .thenCompose(createdNamespace -> patterns.ask(
                shards,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(name),
                    GetNamespaceInfo.apply(name, replyTo, errorTo)),
                GetNamespaceInfoResult.class))
            .thenApply(GetNamespaceInfoResult::getNamespaceInfo);
    }

    @Override
    public CompletionStage<maquette.controller.domain.values.namespace.NamespaceInfo> changeOwner(User executor,
                                                                                                  ResourceName namespaceName,
                                                                                                  Authorization owner) {
        return patterns
            .ask(
                shards,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(namespaceName),
                    ChangeOwner.apply(namespaceName, executor, owner, replyTo, errorTo)),
                ChangedOwner.class)
            .thenCompose(changedOwner -> patterns.ask(
                shards,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(namespaceName),
                    GetNamespaceInfo.apply(namespaceName, replyTo, errorTo)),
                GetNamespaceInfoResult.class))
            .thenApply(GetNamespaceInfoResult::getNamespaceInfo);
    }

    @Override
    public CompletionStage<Done> deleteNamespace(User executor, ResourceName namespaceName) {
        return patterns
            .ask(
                shards,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(namespaceName),
                    DeleteNamespace.apply(namespaceName, executor, replyTo, errorTo)),
                DeletedNamespace.class)
            .thenCompose(deleted -> patterns
                .ask(
                    namespaces,
                    (replyTo, errorTo) -> DeleteNamespace.apply(namespaceName, executor, replyTo, errorTo),
                    DeletedNamespace.class))
            .thenApply(deleted -> Done.getInstance());
    }

    @Override
    public CompletionStage<NamespaceDetails> getNamespaceDetails(User executor, ResourceName namespace) {
        return patterns
            .ask(
                shards,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(namespace),
                    GetNamespaceDetails.apply(namespace, replyTo, errorTo)),
                GetNamespaceDetailsResult.class)
            .thenApply(GetNamespaceDetailsResult::getNamespaceDetails);
    }

    @Override
    public CompletionStage<GrantedAuthorization> grantNamespaceAccess(
        User executor, ResourceName namespaceName, NamespacePrivilege grant, Authorization grantFor) {

        return patterns
            .ask(
                shards,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(namespaceName),
                    GrantNamespaceAccess.apply(namespaceName, executor, grant, grantFor, replyTo, errorTo)),
                GrantedNamespaceAccess.class)
            .thenApply(GrantedNamespaceAccess::getGrantedFor);
    }

    @Override
    public CompletionStage<Set<NamespaceInfo>> listNamespaces(User executor) {
        return patterns.process(result -> CollectNamespaceInfos.create(namespaces, shards, result));
    }

    @Override
    public CompletionStage<GrantedAuthorization> revokeNamespaceAccess(
        User executor, ResourceName namespaceName, NamespacePrivilege revoke, Authorization revokeFrom) {

        return patterns
            .ask(
                shards,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(namespaceName),
                    RevokeNamespaceAccess.apply(namespaceName, executor, revoke, revokeFrom, replyTo, errorTo)),
                RevokedNamespaceAccess.class)
            .thenApply(RevokedNamespaceAccess::getRevokedFrom);
    }

}
