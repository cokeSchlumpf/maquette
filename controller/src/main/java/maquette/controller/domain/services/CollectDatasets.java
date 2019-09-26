package maquette.controller.domain.services;

import java.time.Duration;
import java.util.List;
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
import maquette.controller.domain.entities.dataset.Dataset;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.protocol.results.GetDetailsResult;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.entities.namespace.protocol.queries.ListNamespaces;
import maquette.controller.domain.entities.namespace.protocol.results.ListNamespacesResult;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.namespace.NamespaceInfo;

public final class CollectDatasets {

    private CollectDatasets() {

    }

    public static Behavior<Message> create(
        Set<NamespaceInfo> namespaces,
        ActorRef<ShardingEnvelope<DatasetMessage>> datasets,
        CompletableFuture<Set<DatasetDetails>> result) {

        return Behaviors.setup(actor -> {
            ActorRef<GetDetailsResult> getDetailsResultAdapter =
                actor.messageAdapter(GetDetailsResult.class, GetDetailsResultWrapper::new);

            ActorRef<ErrorMessage> errorMessageAdapter =
                actor.messageAdapter(ErrorMessage.class, ErrorMessageWrapper::new);

            int count = 0;

            for (NamespaceInfo ns : namespaces) {
                for (ResourceName ds : ns.getDatasets()) {
                    ResourcePath path = ResourcePath.apply(ns.getName(), ds);
                    datasets.tell(ShardingEnvelope.apply(
                        Dataset.createEntityId(path),
                        GetDetails.apply(path, getDetailsResultAdapter, errorMessageAdapter)));
                }

                count += ns.getDatasets().size();
            }

            final int count$final = count;

            if (count$final > 0) {
                return Behaviors.withTimers(scheduler -> {
                    scheduler.startSingleTimer("timeout", Timeout.apply(), Duration.ofSeconds(10));
                    return receive(count$final, Sets.newHashSet(), result);
                });
            } else {
                result.complete(ImmutableSet.of());
                return Behavior.stopped();
            }
        });
    }

    private static Behavior<Message> receive(
        int count,
        Set<DatasetDetails> collected,
        CompletableFuture<Set<DatasetDetails>> result) {

        return Behaviors
            .receive(Message.class)
            .onMessage(GetDetailsResultWrapper.class, (ctx, wrapper) -> {
                collected.add(wrapper.result.getDetails());

                if (collected.size() >= count) {
                    result.complete(ImmutableSet.copyOf(collected));
                    return Behavior.stopped();
                } else {
                    return Behavior.same();
                }
            })
            .onMessage(Timeout.class, (ctx, timeout) -> {
                result.complete(ImmutableSet.copyOf(collected));
                return Behaviors.stopped();
            })
            .build();
    }

    interface Message {

    }

    @AllArgsConstructor(staticName = "apply")
    private static class ErrorMessageWrapper implements Message {

        private final ErrorMessage message;

    }

    @AllArgsConstructor(staticName = "apply")
    private static class GetDetailsResultWrapper implements Message {

        private final GetDetailsResult result;

    }

    @AllArgsConstructor(staticName = "apply")
    private static class Timeout implements Message {

    }

}
