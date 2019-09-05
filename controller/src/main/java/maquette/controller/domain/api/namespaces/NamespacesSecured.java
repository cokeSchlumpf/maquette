package maquette.controller.domain.api.namespaces;

import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.namespace.Namespace;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceDetails;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceDetailsResult;
import maquette.controller.domain.exceptions.NotAuthorizedException;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespaceDetails;
import maquette.controller.domain.values.namespace.NamespaceInfo;
import maquette.controller.domain.values.namespace.NamespacePrivilege;

@AllArgsConstructor(staticName = "apply")
public final class NamespacesSecured implements Namespaces {

    private final ActorRef<NamespacesMessage> namespaces;

    private final ActorRef<ShardingEnvelope<NamespaceMessage>> shards;

    private final ActorPatterns patterns;

    private final Namespaces delegate;

    private CompletionStage<NamespaceDetails> getDetails(ResourceName namespace) {
        return patterns
            .ask(
                shards,
                (replyTo, errorTo) ->
                    ShardingEnvelope.apply(
                        Namespace.createEntityId(namespace),
                        GetNamespaceDetails.apply(namespace, replyTo, errorTo)),
                GetNamespaceDetailsResult.class)
            .thenApply(GetNamespaceDetailsResult::getNamespaceDetails);
    }

    @Override
    public CompletionStage<NamespaceInfo> changeOwner(User executor, ResourceName namespaceName, Authorization owner) {
        return getDetails(namespaceName)
            .thenCompose(details -> {
                if (details.getAcl().canChangeOwner(executor)) {
                    return delegate.changeOwner(executor, namespaceName, owner);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<NamespaceInfo> createNamespace(User executor, ResourceName name) {
        if (executor instanceof AuthenticatedUser) {
            return delegate.createNamespace(executor, name);
        } else {
            throw NotAuthorizedException.apply(executor);
        }
    }

    @Override
    public CompletionStage<Done> deleteNamespace(User executor, ResourceName namespaceName) {
        return getDetails(namespaceName)
            .thenCompose(details -> {
                if (details.getAcl().canDeleteNamespace(executor)) {
                    return delegate.deleteNamespace(executor, namespaceName);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<NamespaceDetails> getNamespaceDetails(User executor, ResourceName namespace) {
        return getDetails(namespace)
            .thenCompose(details -> {
                if (details.getAcl().canReadDetails(executor)) {
                    return delegate.getNamespaceDetails(executor, namespace);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<GrantedAuthorization> grantNamespaceAccess(User executor, ResourceName namespaceName,
                                                                      NamespacePrivilege grant, Authorization grantFor) {
        return getDetails(namespaceName)
            .thenCompose(details -> {
                if (details.getAcl().canGrantNamespaceAccess(executor)) {
                    return delegate.grantNamespaceAccess(executor, namespaceName, grant, grantFor);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<Set<NamespaceInfo>> listNamespaces(User executor) {
        return delegate
            .listNamespaces(executor)
            .thenApply(namespaces -> namespaces
                .stream()
                .filter(info -> info.getAcl().canReadDetails(executor))
                .collect(Collectors.toSet()));
    }

    @Override
    public CompletionStage<GrantedAuthorization> revokeNamespaceAccess(User executor, ResourceName namespaceName,
                                                                       NamespacePrivilege revoke, Authorization revokeFrom) {
        return getDetails(namespaceName)
            .thenCompose(details -> {
                if (details.getAcl().canRevokeNamespaceAccess(executor)) {
                    return delegate.revokeNamespaceAccess(executor, namespaceName, revoke, revokeFrom);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

}
