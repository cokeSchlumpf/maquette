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
import maquette.controller.domain.api.projects.Projects;
import maquette.controller.domain.api.projects.ProjectsFactory;
import maquette.controller.domain.api.shop.Shop;
import maquette.controller.domain.api.shop.ShopFactory;
import maquette.controller.domain.api.users.Users;
import maquette.controller.domain.api.users.UsersFactory;
import maquette.controller.domain.entities.dataset.Dataset;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.namespace.Namespace;
import maquette.controller.domain.entities.namespace.NamespacesRegistry;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.entities.project.Project;
import maquette.controller.domain.entities.project.ProjectsRegistry;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.ProjectsMessage;
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

    private final Datasets datasets;

    private final Users users;

    private final Projects projects;

    private final Shop shop;

    public static CoreApplication apply(DataStorageAdapter storageAdapter) {
        return apply(ActorSystem.apply("maquette"), storageAdapter);
    }

    public static CoreApplication apply(
        ActorSystem system, DataStorageAdapter storageAdapter) {

        final ClusterSharding sharding = ClusterSharding.get(Adapter.toTyped(system));
        final ClusterSingleton singleton = ClusterSingleton.createExtension(Adapter.toTyped(system));
        final ActorPatterns patterns = ActorPatterns.apply(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final ActorRef<ShardingEnvelope<DatasetMessage>> datasetShards = createDatasetSharding(sharding, storageAdapter);
        final ActorRef<ShardingEnvelope<NamespaceMessage>> namespaceShards = createNamespaceSharding(sharding);
        final ActorRef<ShardingEnvelope<UserMessage>> userShards = createUserSharding(sharding);
        final ActorRef<ShardingEnvelope<ProjectMessage>> projectShards = createProjectSharding(sharding);
        final ActorRef<NamespacesMessage> namespacesRegistry = singleton.init(NamespacesRegistry.create());
        final ActorRef<ProjectsMessage> projectsRegistry = singleton.init(ProjectsRegistry.create());

        final CreateDefaultNamespace createDefaultNamespace = CreateDefaultNamespace.apply(
            namespacesRegistry, namespaceShards, userShards, patterns);

        final Datasets datasets = DatasetsFactory
            .apply(namespaceShards, datasetShards, userShards, patterns, createDefaultNamespace, materializer)
            .create();

        final Users users = UsersFactory
            .apply(namespacesRegistry, namespaceShards, datasetShards, userShards, patterns, createDefaultNamespace)
            .create();

        final Projects projects = ProjectsFactory
            .apply(projectsRegistry, namespacesRegistry, projectShards, namespaceShards, datasetShards, patterns, createDefaultNamespace)
            .create();

        final Shop shop = ShopFactory
            .apply(projectsRegistry, namespacesRegistry, projectShards, namespaceShards, datasetShards, patterns, createDefaultNamespace)
            .create();

        // initialize application
        return CoreApplication.apply(system, datasets, users, projects, shop);
    }

    private static ActorRef<ShardingEnvelope<DatasetMessage>> createDatasetSharding(ClusterSharding sharding, DataStorageAdapter storageAdapter) {
        final Entity<DatasetMessage, ShardingEnvelope<DatasetMessage>> datasetEntity = Entity
            .ofPersistentEntity(
                Dataset.ENTITY_KEY,
                ctx -> Dataset.create(ctx.getActorContext(), storageAdapter, ResourcePath.apply(ctx.getEntityId())));
        return sharding.init(datasetEntity);
    }

    private static ActorRef<ShardingEnvelope<NamespaceMessage>> createNamespaceSharding(ClusterSharding sharding) {
        final Entity<NamespaceMessage, ShardingEnvelope<NamespaceMessage>> namespaceEntity = Entity
            .ofPersistentEntity(
                Namespace.ENTITY_KEY,
                ctx -> Namespace.create(ctx.getActorContext(), ResourceName.apply(ctx.getEntityId())));
        return sharding.init(namespaceEntity);
    }

    private static ActorRef<ShardingEnvelope<UserMessage>> createUserSharding(ClusterSharding sharding) {
        final Entity<UserMessage, ShardingEnvelope<UserMessage>> userEntity = Entity
            .ofPersistentEntity(
                User.ENTITY_KEY,
                ctx -> User.create(User.fromEntityId(ctx.getEntityId())));

        return sharding.init(userEntity);
    }

    private static ActorRef<ShardingEnvelope<ProjectMessage>> createProjectSharding(ClusterSharding sharding) {
        final Entity<ProjectMessage, ShardingEnvelope<ProjectMessage>> projectEntity = Entity
            .ofPersistentEntity(
                Project.ENTITY_KEY,
                ctx -> Project.create(Project.fromEntityId(ctx.getEntityId())));

        return sharding.init(projectEntity);
    }

    public Datasets datasets() {
        return datasets;
    }

    public Projects projects() { return projects; }

    public Shop shop() { return shop; }

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
