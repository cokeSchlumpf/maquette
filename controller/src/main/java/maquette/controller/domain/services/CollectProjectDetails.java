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
import maquette.controller.domain.entities.project.Project;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.ProjectsMessage;
import maquette.controller.domain.entities.project.protocol.queries.GetProjectDetails;
import maquette.controller.domain.entities.project.protocol.queries.ListProjects;
import maquette.controller.domain.entities.project.protocol.results.GetProjectDetailsResult;
import maquette.controller.domain.entities.project.protocol.results.ListProjectsResult;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.project.ProjectDetails;

public final class CollectProjectDetails {

    public static Behavior<Message> create(
        ActorRef<ProjectsMessage> namespacesRegistry,
        ActorRef<ShardingEnvelope<ProjectMessage>> sharding,
        CompletableFuture<Set<ProjectDetails>> result) {

        return Behaviors.setup(actor -> {
            final ActorRef<ListProjectsResult> listNamespacesResultAdapter =
                actor.messageAdapter(ListProjectsResult.class, ListNamespacesResultWrapper::new);

            final ActorRef<ErrorMessage> errorMessageAdapter =
                actor.messageAdapter(ErrorMessage.class, ErrorMessageWrapper::new);

            final ListProjects request =
                ListProjects.apply(listNamespacesResultAdapter, errorMessageAdapter);

            namespacesRegistry.tell(request);

            return Behaviors.withTimers(scheduler -> {
                scheduler.startSingleTimer("timeout", Timeout.apply(), Duration.ofSeconds(10));

                return Behaviors
                    .receive(Message.class)
                    .onMessage(ListNamespacesResultWrapper.class, (ctx, wrapper) -> {
                        Set<ResourceName> namespaces = wrapper.result.getProjects();
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
        Set<ResourceName> namespaces, ActorRef<ShardingEnvelope<ProjectMessage>> sharding,
        CompletableFuture<Set<ProjectDetails>> result) {

        if (namespaces.isEmpty()) {
            result.complete(ImmutableSet.of());
            return Behaviors.stopped();
        } else {
            final ActorRef<GetProjectDetailsResult> resultAdapter =
                actor.messageAdapter(GetProjectDetailsResult.class, GetNamespaceInfoResultWrapper::apply);
            final ActorRef<ErrorMessage> errorMessageAdapter =
                actor.messageAdapter(ErrorMessage.class, ErrorMessageWrapper::new);

            namespaces.forEach(name -> {
                final String entityId = Project.createEntityId(name);
                final GetProjectDetails msg = GetProjectDetails.apply(name, resultAdapter, errorMessageAdapter);
                sharding.tell(ShardingEnvelope.apply(entityId, msg));
            });

            return collecting(actor, namespaces.size(), Sets.newHashSet(), result);
        }
    }

    private static Behavior<Message> collecting(
        ActorContext<Message> actor, int count, Set<ProjectDetails> collected,
        CompletableFuture<Set<ProjectDetails>> result) {

        return Behaviors
            .receive(Message.class)
            .onMessage(GetNamespaceInfoResultWrapper.class, (ctx, wrapper) -> {
                actor.getLog().debug("Received details " + wrapper.result.getDetails());
                collected.add(wrapper.result.getDetails());

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

        private final GetProjectDetailsResult result;

    }

    @AllArgsConstructor(staticName = "apply")
    private static class ListNamespacesResultWrapper implements Message {

        private final ListProjectsResult result;

    }

    @AllArgsConstructor(staticName = "apply")
    private static class ErrorMessageWrapper implements Message {

        private final ErrorMessage message;

    }

    @AllArgsConstructor(staticName = "apply")
    private static class Timeout implements Message {

    }

}
