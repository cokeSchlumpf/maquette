package maquette.controller.domain.services;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.namespace.Namespace;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceInfo;
import maquette.controller.domain.entities.namespace.protocol.queries.ListNamespaces;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceInfoResult;
import maquette.controller.domain.entities.namespace.protocol.results.ListNamespacesResult;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.namespace.NamespaceInfo;

public final class CollectNamespaceInfos {

    public static Behavior<Message> create(
        ActorRef<NamespacesMessage> namespacesRegistry,
        ActorRef<ShardingEnvelope<NamespaceMessage>> sharding,
        CompletableFuture<Set<NamespaceInfo>> result) {

        return Behaviors.setup(actor -> {
            final ActorRef<ListNamespacesResult> listNamespacesResultAdapter =
                actor.messageAdapter(ListNamespacesResult.class, ListNamespacesResultWrapper::new);

            final ActorRef<ErrorMessage> errorMessageAdapter =
                actor.messageAdapter(ErrorMessage.class, ErrorMessageWrapper::new);

            final ListNamespaces request =
                ListNamespaces.apply(listNamespacesResultAdapter, errorMessageAdapter);

            namespacesRegistry.tell(request);

            return Behaviors.withTimers(scheduler -> {
                scheduler.startSingleTimer("timeout", Timeout.apply(), Duration.ofSeconds(10));

                return Behaviors
                    .receive(Message.class)
                    .onMessage(ListNamespacesResultWrapper.class, (ctx, wrapper) -> {
                        Set<ResourceName> namespaces = wrapper.result.getNamespaces();
                        return requestInfos(ctx, namespaces, sharding, result);
                    })
                    .onMessage(Timeout.class, (ctx, timeout) -> {
                        result.complete(ImmutableSet.of());
                        return Behaviors.stopped();
                    })
                    .build();
            });
        });
    }

    private static Behavior<Message> requestInfos(
        ActorContext<Message> actor,
        Set<ResourceName> namespaces, ActorRef<ShardingEnvelope<NamespaceMessage>> sharding,
        CompletableFuture<Set<NamespaceInfo>> result) {

        if (namespaces.isEmpty()) {
            result.complete(ImmutableSet.of());
            return Behaviors.stopped();
        } else {
            final ActorRef<GetNamespaceInfoResult> resultAdapter =
                actor.messageAdapter(GetNamespaceInfoResult.class, GetNamespaceInfoResultWrapper::apply);
            final ActorRef<ErrorMessage> errorMessageAdapter =
                actor.messageAdapter(ErrorMessage.class, ErrorMessageWrapper::new);

            namespaces.forEach(name -> {
                final String entityId = Namespace.createEntityId(name);
                final GetNamespaceInfo msg = GetNamespaceInfo.apply(name, resultAdapter, errorMessageAdapter);
                sharding.tell(ShardingEnvelope.apply(entityId, msg));
            });

            return collecting(actor, namespaces.size(), Sets.newHashSet(), result);
        }
    }

    private static Behavior<Message> collecting(
        ActorContext<Message> actor, int count, Set<NamespaceInfo> collected,
        CompletableFuture<Set<NamespaceInfo>> result) {

        return Behaviors
            .receive(Message.class)
            .onMessage(GetNamespaceInfoResultWrapper.class, (ctx, wrapper) -> {
                actor.getLog().debug("Received info " + wrapper.result.getNamespaceInfo());
                collected.add(wrapper.result.getNamespaceInfo());

                if (count <= collected.size()) {
                    result.complete(ImmutableSet.copyOf(collected));
                    return Behaviors.stopped();
                } else {
                    return collecting(actor, count, collected, result);
                }
            })
            .onMessage(Timeout.class, (ctx, timeout) -> {
                actor.getLog().debug("Received timeout.");

                result.complete(ImmutableSet.copyOf(collected));
                return Behaviors.stopped();
            })
            .build();
    }

    interface Message {

    }

    @AllArgsConstructor(staticName = "apply")
    private static class GetNamespaceInfoResultWrapper implements Message {

        private final GetNamespaceInfoResult result;

    }

    @AllArgsConstructor(staticName = "apply")
    private static class ListNamespacesResultWrapper implements Message {

        private final ListNamespacesResult result;

    }

    @AllArgsConstructor(staticName = "apply")
    private static class ErrorMessageWrapper implements Message {

        private final ErrorMessage message;

    }

    @AllArgsConstructor(staticName = "apply")
    private static class Timeout implements Message {

    }

}
