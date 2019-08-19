package maquette.controller.domain.entities.namespace.services;

import java.time.Duration;
import java.util.Set;

import com.google.common.collect.Sets;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.namespace.Namespace;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceInfo;
import maquette.controller.domain.entities.namespace.protocol.queries.ListNamespaces;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceInfoResult;
import maquette.controller.domain.entities.namespace.protocol.results.ListNamespacesResult;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.namespace.NamespaceInfo;

public final class CollectNamespaceInfos {

    public static Behavior<Message> create(
        Set<ResourceName> namespaces, ActorRef<ShardingEnvelope<NamespaceMessage>> sharding, ListNamespaces request) {

        if (namespaces.isEmpty()) {
            ListNamespacesResult result = ListNamespacesResult.apply(Sets.newHashSet());
            request.getReplyTo().tell(result);
            return Behaviors.stopped();
        } else {
            return Behaviors.setup(actor -> {
                final ActorRef<GetNamespaceInfoResult> resultAdapter =
                    actor.messageAdapter(GetNamespaceInfoResult.class, GetNamespaceInfoResultWrapper::apply);

                return Behaviors.withTimers(scheduler -> {
                    namespaces.forEach(name -> {
                        final String entityId = Namespace.createEntityId(name);
                        final GetNamespaceInfo msg = GetNamespaceInfo.apply(name, resultAdapter);
                        sharding.tell(ShardingEnvelope.apply(entityId, msg));
                    });

                    scheduler.startSingleTimer("timeout", Timeout.apply(), Duration.ofSeconds(10));
                    return collecting(actor, namespaces.size(), Sets.newHashSet(), request);
                });
            });
        }
    }

    private static Behavior<Message> collecting(
        ActorContext<Message> actor, int count, Set<NamespaceInfo> collected,
        ListNamespaces request) {

        return Behaviors
            .receive(Message.class)
            .onMessage(GetNamespaceInfoResultWrapper.class, (ctx, wrapper) -> {
                actor.getLog().debug("Received info " + wrapper.result.getNamespaceInfo());
                collected.add(wrapper.result.getNamespaceInfo());

                if (count <= collected.size()) {
                    final ListNamespacesResult result = ListNamespacesResult.apply(collected);
                    request.getReplyTo().tell(result);
                    return Behaviors.stopped();
                } else {
                    return collecting(actor, count, collected, request);
                }
            })
            .onMessage(Timeout.class, (ctx, timeout) -> {
                actor.getLog().debug("Received timeout.");

                final ListNamespacesResult result = ListNamespacesResult.apply(collected);
                request.getReplyTo().tell(result);
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
    private static class Timeout implements Message {

    }

}
