package maquette.controller.domain;

import java.io.PrintStream;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.commands.shop.ListDatasetsCmd;
import maquette.controller.domain.api.commands.commands.shop.ListProjectsCmd;
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
import maquette.controller.domain.entities.notifcation.Notifications;
import maquette.controller.domain.entities.notifcation.protocol.NotificationsMessage;
import maquette.controller.domain.entities.project.Project;
import maquette.controller.domain.entities.project.ProjectRegistry;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.ProjectsMessage;
import maquette.controller.domain.entities.user.User;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.ports.DataStorageAdapter;
import maquette.controller.domain.services.CreateDefaultProject;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.util.databind.ObjectMapperFactory;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.AuthenticatedUser;
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
        final ActorRef<ShardingEnvelope<ProjectMessage>> projectShards = createNamespaceSharding(sharding);
        final ActorRef<ShardingEnvelope<UserMessage>> userShards = createUserSharding(sharding);
        final ActorRef<ProjectsMessage> projectsRegistry = singleton.init(ProjectRegistry.create());
        final ActorRef<NotificationsMessage> notifications = singleton.init(Notifications.create());

        final CreateDefaultProject createDefaultProject = CreateDefaultProject.apply(
            projectsRegistry, projectShards, userShards, patterns);

        final Datasets datasets = DatasetsFactory
            .apply(projectShards, datasetShards, userShards, notifications, patterns, createDefaultProject, materializer)
            .create();

        final Users users = UsersFactory
            .apply(userShards, notifications, patterns, createDefaultProject)
            .create();

        final Projects projects = ProjectsFactory
            .apply(projectsRegistry, projectShards, datasetShards, patterns, createDefaultProject)
            .create();

        final Shop shop = ShopFactory
            .apply(projectsRegistry, projectShards, datasetShards, patterns, createDefaultProject)
            .create();

        // initialize application
        return CoreApplication.apply(system, datasets, users, projects, shop);
    }

    private static ActorRef<ShardingEnvelope<DatasetMessage>> createDatasetSharding(ClusterSharding sharding,
                                                                                    DataStorageAdapter storageAdapter) {
        final Entity<DatasetMessage, ShardingEnvelope<DatasetMessage>> datasetEntity = Entity
            .ofPersistentEntity(
                Dataset.ENTITY_KEY,
                ctx -> Dataset.create(ctx.getActorContext(), storageAdapter, ResourcePath.apply(ctx.getEntityId())));
        return sharding.init(datasetEntity);
    }

    private static ActorRef<ShardingEnvelope<ProjectMessage>> createNamespaceSharding(ClusterSharding sharding) {
        final Entity<ProjectMessage, ShardingEnvelope<ProjectMessage>> namespaceEntity = Entity
            .ofPersistentEntity(
                Project.ENTITY_KEY,
                ctx -> Project.create(ctx.getActorContext(), ResourceName.apply(ctx.getEntityId())));
        return sharding.init(namespaceEntity);
    }

    private static ActorRef<ShardingEnvelope<UserMessage>> createUserSharding(ClusterSharding sharding) {
        final Entity<UserMessage, ShardingEnvelope<UserMessage>> userEntity = Entity
            .ofPersistentEntity(
                User.ENTITY_KEY,
                ctx -> User.create(User.fromEntityId(ctx.getEntityId())));

        return sharding.init(userEntity);
    }

    public Datasets datasets() {
        return datasets;
    }

    public Projects projects() {
        return projects;
    }

    public Shop shop() {
        return shop;
    }

    public Users users() {
        return users;
    }

    public void printStatus(PrintStream ps) {
        Operators.suppressExceptions(() -> {
            ObjectMapper om = ObjectMapperFactory.apply().create(true);
            StringBuilder sb = new StringBuilder();

            sb.append("\n");
            sb.append("SYSTEM STATUS\n");
            sb.append("-------------\n");

            sb.append(
                ListProjectsCmd
                    .apply()
                    .run(AuthenticatedUser.admin(), this, OutputFormat.apply())
                    .toCompletableFuture()
                    .get()
                    .toCommandResult(om)
                    .getOutput());

            sb.append("\n\n");

            sb.append(
                ListDatasetsCmd
                    .apply()
                    .run(AuthenticatedUser.admin(), this, OutputFormat.apply())
                    .toCompletableFuture()
                    .get()
                    .toCommandResult(om)
                    .getOutput());

            sb.append("\n\n");

            ps.println(sb);
        });
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
