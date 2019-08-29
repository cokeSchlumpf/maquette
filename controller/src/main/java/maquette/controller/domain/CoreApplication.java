package maquette.controller.domain;

import java.util.concurrent.ExecutionException;

import akka.actor.ActorSystem;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.Adapter;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.typed.ClusterSingleton;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import maquette.controller.domain.api.Datasets;
import maquette.controller.domain.api.DatasetsImpl;
import maquette.controller.domain.api.Namespaces;
import maquette.controller.domain.api.NamespacesFactory;
import maquette.controller.domain.api.NamespacesImpl;
import maquette.controller.domain.api.NamespacesSecured;
import maquette.controller.domain.entities.dataset.Dataset;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.namespace.Namespace;
import maquette.controller.domain.entities.namespace.NamespacesRegistry;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import scala.compat.java8.FutureConverters;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public class CoreApplication {

    private final ActorSystem system;

    private final Namespaces namespaces;

    private final Datasets datasets;

    public static CoreApplication apply() {
        final ActorSystem system = ActorSystem.apply("maquette");
        final ClusterSharding sharding = ClusterSharding.get(Adapter.toTyped(system));
        final ClusterSingleton singleton = ClusterSingleton.createExtension(Adapter.toTyped(system));
        final ActorPatterns patterns = ActorPatterns.apply(system);

        final Entity<DatasetMessage, ShardingEnvelope<DatasetMessage>> datasetEntity = Entity
            .ofPersistentEntity(
                Dataset.ENTITY_KEY,
                ctx -> Dataset.create(ctx.getActorContext(), ResourcePath.apply(ctx.getEntityId())));
        final ActorRef<ShardingEnvelope<DatasetMessage>> datasetShards = sharding.init(datasetEntity);

        // initialize namespace shards
        final Entity<NamespaceMessage, ShardingEnvelope<NamespaceMessage>> namespaceEntity = Entity
            .ofPersistentEntity(
                Namespace.ENTITY_KEY,
                ctx -> Namespace.create(ctx.getActorContext(), ResourceName.apply(ctx.getEntityId())));
        final ActorRef<ShardingEnvelope<NamespaceMessage>> namespaceShards = sharding.init(namespaceEntity);

        // initialize namespace registry
        final ActorRef<NamespacesMessage> namespacesRegistry = singleton.init(NamespacesRegistry.create());
        final Namespaces namespaces = NamespacesFactory
            .apply(namespacesRegistry, namespaceShards, patterns)
            .create();

        final DatasetsImpl datasets = DatasetsImpl.apply(namespaceShards, datasetShards, patterns);

        // initialize application
        return CoreApplication.apply(system, namespaces, datasets);
    }

    public Datasets datasets() {
        return datasets;
    }

    public Namespaces namespaces() {
        return namespaces;
    }

    public void terminate() {
        try {
            FutureConverters
                .toJava(system.terminate())
                .toCompletableFuture()
                .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
