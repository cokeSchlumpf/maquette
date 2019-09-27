package maquette.controller.domain.api.namespaces;

import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.services.CreateDefaultNamespace;
import maquette.controller.domain.util.ActorPatterns;

@AllArgsConstructor(staticName = "apply")
public final class NamespacesFactory {

    private final ActorRef<NamespacesMessage> namespaces;

    private final ActorRef<ShardingEnvelope<NamespaceMessage>> shards;

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

    private final ActorPatterns patterns;

    private final CreateDefaultNamespace createDefaultNamespace;

    public Namespaces create() {
        NamespacesImpl impl = NamespacesImpl.apply(namespaces, shards, datasets, patterns);
        NamespacesSecured secured = NamespacesSecured.apply(namespaces, shards, patterns, impl);
        return NamespacesUserActivity.apply(secured, createDefaultNamespace);
    }

}
