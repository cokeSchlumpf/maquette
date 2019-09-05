package maquette.controller.domain.api.namespaces;

import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.util.ActorPatterns;

@AllArgsConstructor(staticName = "apply")
public final class NamespacesFactory {

    private final ActorRef<NamespacesMessage> namespaces;

    private final ActorRef<ShardingEnvelope<NamespaceMessage>> shards;

    private final ActorPatterns patterns;

    public Namespaces create() {
        NamespacesImpl impl = NamespacesImpl.apply(namespaces, shards, patterns);
        return NamespacesSecured.apply(namespaces, shards, patterns, impl);
    }

}
