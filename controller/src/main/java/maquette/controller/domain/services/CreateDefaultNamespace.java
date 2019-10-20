package maquette.controller.domain.services;

import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.project.Project;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.ProjectsMessage;
import maquette.controller.domain.entities.project.protocol.commands.CreateProject;
import maquette.controller.domain.entities.project.protocol.events.CreatedProject;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.entities.user.protocol.commands.ConfigureNamespace;
import maquette.controller.domain.entities.user.protocol.events.ConfiguredNamespace;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(staticName = "apply")
public final class CreateDefaultNamespace {

    private final ActorRef<ProjectsMessage> namespaces;

    private final ActorRef<ShardingEnvelope<ProjectMessage>> namespacesShards;

    private final ActorRef<ShardingEnvelope<UserMessage>> userShards;

    private final ActorPatterns patterns;

    public <T> CompletionStage<T> run(User executor, Supplier<CompletionStage<T>> andThen) {
        // TODO: Check whether namespace really belongs to user (or not if it already existed before)

        if (executor instanceof AuthenticatedUser) {
            final ResourceName defaultNamespace = ResourceName.apply(executor.getUserId().getId());

            return patterns
                .ask(
                    namespaces,
                    (replyTo, errorTo) -> CreateProject.apply(defaultNamespace, executor, replyTo, errorTo),
                    CreatedProject.class)
                .thenCompose(createdNamespace -> patterns.ask(
                    namespacesShards,
                    (replyTo, errorTo) -> ShardingEnvelope.apply(
                        Project.createEntityId(defaultNamespace),
                        CreateProject.apply(defaultNamespace, executor, replyTo, errorTo)),
                    CreatedProject.class))
                .thenCompose(createdNamespace -> patterns.ask(
                    userShards,
                    (replyTo, errorTo) -> ShardingEnvelope.apply(
                        maquette.controller.domain.entities.user.User.createEntityId(executor.getUserId()),
                        ConfigureNamespace.apply(executor, defaultNamespace, replyTo, errorTo)),
                    ConfiguredNamespace.class))
                .thenCompose(configured -> andThen.get());
        } else {
            return andThen.get();
        }
    }

}
