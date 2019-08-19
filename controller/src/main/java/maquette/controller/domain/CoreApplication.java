package maquette.controller.domain;

import akka.actor.ActorSystem;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.Adapter;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.typed.ClusterSingleton;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import maquette.controller.domain.api.Namespaces;
import maquette.controller.domain.entities.namespace.Namespace;
import maquette.controller.domain.entities.namespace.NamespacesRegistry;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.ResourceName;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public class CoreApplication {

    private final ActorSystem system;

    private final Namespaces namespaces;

    public static CoreApplication apply() {
        final ActorSystem system = ActorSystem.apply("marquette-controller");
        final ClusterSharding sharding = ClusterSharding.get(Adapter.toTyped(system));
        final ClusterSingleton singleton = ClusterSingleton.createExtension(Adapter.toTyped(system));
        final ActorPatterns patterns = ActorPatterns.apply(system);

        // initialize namespace shards
        final Entity<NamespaceMessage, ShardingEnvelope<NamespaceMessage>> namespaceEntity = Entity
            .ofPersistentEntity(
                Namespace.ENTITY_KEY,
                ctx -> Namespace.create(ctx.getActorContext(), ResourceName.apply(ctx.getEntityId())));
        final ActorRef<ShardingEnvelope<NamespaceMessage>> namespaceShards = sharding.init(namespaceEntity);

        // initialize namespace registry
        final ActorRef<NamespacesMessage> namespacesRegistry = singleton.init(NamespacesRegistry.create());
        final Namespaces namespaces = Namespaces.apply(namespacesRegistry, namespaceShards, patterns);

        // initialize application
        return CoreApplication.apply(system, namespaces);
    }

    public Namespaces namespaces() {
        return namespaces;
    }

}
