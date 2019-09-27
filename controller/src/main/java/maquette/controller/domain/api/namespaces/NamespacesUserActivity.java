package maquette.controller.domain.api.namespaces;

import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.services.CreateDefaultNamespace;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespaceDetails;
import maquette.controller.domain.values.namespace.NamespaceInfo;
import maquette.controller.domain.values.namespace.NamespacePrivilege;

@AllArgsConstructor(staticName = "apply")
public final class NamespacesUserActivity implements Namespaces {

    private final Namespaces delegate;

    private final CreateDefaultNamespace createDefaultNamespace;

    private <T> CompletionStage<T> createDefaultNamespace(User executor, Function<Namespaces, CompletionStage<T>> andThen) {
        return createDefaultNamespace.run(executor, () -> andThen.apply(delegate));
    }

    @Override
    public CompletionStage<NamespaceInfo> changeOwner(User executor, ResourceName namespaceName, Authorization owner) {
        return createDefaultNamespace(executor, n -> n.changeOwner(executor, namespaceName, owner));
    }

    @Override
    public CompletionStage<NamespaceInfo> createNamespace(User executor, ResourceName name) {
        return createDefaultNamespace(executor, n -> n.createNamespace(executor, name));
    }

    @Override
    public CompletionStage<Done> deleteNamespace(User executor, ResourceName namespaceName) {
        return createDefaultNamespace(executor, n -> n.deleteNamespace(executor, namespaceName));
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> getDatasets(User executor, ResourceName namespace) {
        return createDefaultNamespace(executor, n -> n.getDatasets(executor, namespace));
    }

    @Override
    public CompletionStage<NamespaceDetails> getNamespaceDetails(User executor, ResourceName namespace) {
        return createDefaultNamespace(executor, n -> n.getNamespaceDetails(executor, namespace));
    }

    @Override
    public CompletionStage<GrantedAuthorization> grantNamespaceAccess(User executor, ResourceName namespaceName,
                                                                      NamespacePrivilege grant, Authorization grantFor) {
        return createDefaultNamespace(executor, n -> n.grantNamespaceAccess(executor, namespaceName, grant, grantFor));
    }

    @Override
    public CompletionStage<Set<NamespaceInfo>> listNamespaces(User executor) {
        return createDefaultNamespace(executor, n -> n.listNamespaces(executor));
    }

    @Override
    public CompletionStage<GrantedAuthorization> revokeNamespaceAccess(User executor, ResourceName namespaceName,
                                                                       NamespacePrivilege revoke, Authorization revokeFrom) {
        return createDefaultNamespace(executor, n -> n.revokeNamespaceAccess(executor, namespaceName, revoke, revokeFrom));
    }

}
