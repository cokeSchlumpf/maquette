package maquette.controller.domain;

import java.util.concurrent.ExecutionException;

import akka.actor.ActorSystem;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.Adapter;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.typed.ClusterSingleton;
import akka.stream.ActorMaterializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.domain.api.datasets.Datasets;
import maquette.controller.domain.api.datasets.DatasetsFactory;
import maquette.controller.domain.api.namespaces.Namespaces;
import maquette.controller.domain.api.namespaces.NamespacesFactory;
import maquette.controller.domain.api.users.Users;
import maquette.controller.domain.api.users.UsersFactory;
import maquette.controller.domain.entities.dataset.Dataset;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.namespace.Namespace;
import maquette.controller.domain.entities.namespace.NamespacesRegistry;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.entities.user.User;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.ports.DataStorageAdapter;
import maquette.controller.domain.services.CreateDefaultNamespace;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import scala.compat.java8.FutureConverters;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public class CoreApplication {

    private final ActorSystem system;

    private final Namespaces namespaces;

    private final Datasets datasets;

    private final Users users;

    public static CoreApplication apply(DataStorageAdapter storageAdapter) {
        return apply(ActorSystem.apply("maquette"), storageAdapter);
    }

    public static CoreApplication apply(
        ActorSystem system, DataStorageAdapter storageAdapter) {

        final ClusterSharding sharding = ClusterSharding.get(Adapter.toTyped(system));
        final ClusterSingleton singleton = ClusterSingleton.createExtension(Adapter.toTyped(system));
        final ActorPatterns patterns = ActorPatterns.apply(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final Entity<DatasetMessage, ShardingEnvelope<DatasetMessage>> datasetEntity = Entity
            .ofPersistentEntity(
                Dataset.ENTITY_KEY,
                ctx -> Dataset.create(ctx.getActorContext(), storageAdapter, ResourcePath.apply(ctx.getEntityId())));
        final ActorRef<ShardingEnvelope<DatasetMessage>> datasetShards = sharding.init(datasetEntity);

        // initialize namespace shards
        final Entity<NamespaceMessage, ShardingEnvelope<NamespaceMessage>> namespaceEntity = Entity
            .ofPersistentEntity(
                Namespace.ENTITY_KEY,
                ctx -> Namespace.create(ctx.getActorContext(), ResourceName.apply(ctx.getEntityId())));
        final ActorRef<ShardingEnvelope<NamespaceMessage>> namespaceShards = sharding.init(namespaceEntity);

        final Entity<UserMessage, ShardingEnvelope<UserMessage>> userEntity = Entity
            .ofPersistentEntity(
                User.ENTITY_KEY,
                ctx -> User.create(User.fromEntityId(ctx.getEntityId())));
        final ActorRef<ShardingEnvelope<UserMessage>> userShards = sharding.init(userEntity);

        // initialize namespace registry
        final ActorRef<NamespacesMessage> namespacesRegistry = singleton.init(NamespacesRegistry.create());

        final CreateDefaultNamespace createDefaultNamespace = CreateDefaultNamespace.apply(
            namespacesRegistry, namespaceShards, patterns);

        final Namespaces namespaces = NamespacesFactory
            .apply(namespacesRegistry, namespaceShards, datasetShards, patterns, createDefaultNamespace)
            .create();

        final Datasets datasets = DatasetsFactory
            .apply(namespacesRegistry, namespaceShards, datasetShards, userShards, patterns, createDefaultNamespace, materializer)
            .create();

        final Users users = UsersFactory
            .apply(namespaceShards, userShards, patterns, createDefaultNamespace)
            .create();

        // initialize application
        return CoreApplication.apply(system, namespaces, datasets, users);
    }

    public Datasets datasets() {
        return datasets;
    }

    public Namespaces namespaces() {
        return namespaces;
    }

    public Users users() {
        return users;
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
