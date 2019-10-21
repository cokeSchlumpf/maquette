package maquette.controller.domain.services;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.Dataset;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.protocol.results.GetDetailsResult;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.project.ProjectDetails;

public final class CollectDatasets {

    private static final Logger LOG = LoggerFactory.getLogger(CollectDatasets.class);

    private CollectDatasets() {

    }

    public static Behavior<Message> create(
        Set<ProjectDetails> projects,
        ActorRef<ShardingEnvelope<DatasetMessage>> datasets,
        CompletableFuture<Set<DatasetDetails>> result) {

        return Behaviors.setup(actor -> {
            ActorRef<GetDetailsResult> getDetailsResultAdapter =
                actor.messageAdapter(GetDetailsResult.class, GetDetailsResultWrapper::new);

            ActorRef<ErrorMessage> errorMessageAdapter =
                actor.messageAdapter(ErrorMessage.class, ErrorMessageWrapper::new);

            int count = 0;

            for (ProjectDetails proj : projects) {
                for (ResourceName ds : proj.getDatasets()) {
                    ResourcePath path = ResourcePath.apply(proj.getName(), ds);
                    datasets.tell(ShardingEnvelope.apply(
                        Dataset.createEntityId(path),
                        GetDetails.apply(path, getDetailsResultAdapter, errorMessageAdapter)));
                }

                count += proj.getDatasets().size();
            }

            final int count$final = count;

            if (count$final > 0) {
                return Behaviors.withTimers(scheduler -> {
                    scheduler.startSingleTimer("timeout", Timeout.apply(), Duration.ofSeconds(10));
                    return receive(count$final, 0, Sets.newHashSet(), result);
                });
            } else {
                result.complete(ImmutableSet.of());
                return Behavior.stopped();
            }
        });
    }

    private static Behavior<Message> receive(
        int count,
        int errors,
        Set<DatasetDetails> collected,
        CompletableFuture<Set<DatasetDetails>> result) {

        return Behaviors
            .receive(Message.class)
            .onMessage(GetDetailsResultWrapper.class, (ctx, wrapper) -> {
                collected.add(wrapper.result.getDetails());

                if (collected.size() >= count + errors) {
                    result.complete(ImmutableSet.copyOf(collected));
                    return Behavior.stopped();
                } else {
                    return receive(count, errors, collected, result);
                }
            })
            .onMessage(ErrorMessageWrapper.class, (ctx, wrapper) -> {
                LOG.debug(String.format(
                    "Received error message while collecting dataset details: %s",
                    wrapper.message.toString()));

                if (collected.size() >= count + errors) {
                    result.complete(ImmutableSet.copyOf(collected));
                    return Behavior.stopped();
                } else {
                    return receive(count, errors + 1, collected, result);
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
