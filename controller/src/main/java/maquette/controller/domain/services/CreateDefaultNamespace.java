package maquette.controller.domain.services;

import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.namespace.Namespace;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.entities.namespace.protocol.commands.CreateNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.CreatedNamespace;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(staticName = "apply")
public final class CreateDefaultNamespace {

    private final ActorRef<NamespacesMessage> namespaces;

    private final ActorRef<ShardingEnvelope<NamespaceMessage>> namespacesShards;

    private final ActorPatterns patterns;

    public <T> CompletionStage<T> run(User executor, Supplier<CompletionStage<T>> andThen) {
        if (executor instanceof AuthenticatedUser) {
            final ResourceName defaultNamespace = ResourceName.apply(executor.getUserId().getId());

            return patterns
                .ask(
                    namespaces,
                    (replyTo, errorTo) -> CreateNamespace.apply(defaultNamespace, executor, replyTo, errorTo),
                    CreatedNamespace.class)
                .thenCompose(createdNamespace -> patterns.ask(
                    namespacesShards,
                    (replyTo, errorTo) -> ShardingEnvelope.apply(
                        Namespace.createEntityId(defaultNamespace),
                        CreateNamespace.apply(defaultNamespace, executor, replyTo, errorTo)),
                    CreatedNamespace.class))
                .thenCompose(created -> andThen.get());
        } else {
            return andThen.get();
        }
    }

}
