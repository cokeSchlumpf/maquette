package maquette.controller.domain.entities.namespace;

import java.time.Instant;
import java.util.Set;

import com.google.common.collect.Sets;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.typed.SingletonActor;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EventHandler;
import akka.persistence.typed.javadsl.EventSourcedBehavior;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.controller.domain.entities.namespace.protocol.NamespacesEvent;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.entities.namespace.protocol.commands.CreateNamespace;
import maquette.controller.domain.entities.namespace.protocol.commands.DeleteNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.CreatedNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.DeletedNamespace;
import maquette.controller.domain.entities.namespace.protocol.queries.ListNamespaces;
import maquette.controller.domain.entities.namespace.protocol.results.ListNamespacesResult;
import maquette.controller.domain.values.core.ResourceName;

public final class NamespacesRegistry extends EventSourcedBehavior<NamespacesMessage, NamespacesEvent, NamespacesRegistry.State> {

    private static final String PERSISTENCE_ID = "resource-registry";

    private NamespacesRegistry() {
        super(PersistenceId.apply(PERSISTENCE_ID));
    }

    public static SingletonActor<NamespacesMessage> create() {
        Behavior<NamespacesMessage> behavior = Behaviors.setup(actor -> new NamespacesRegistry());
        return SingletonActor.apply(behavior, PERSISTENCE_ID);
    }

    @Override
    public State emptyState() {
        return State.apply();
    }

    @Override
    public CommandHandler<NamespacesMessage, NamespacesEvent, State> commandHandler() {
        return newCommandHandlerBuilder()
            .forAnyState()
            .onCommand(CreateNamespace.class, this::onCreateNamespace)
            .onCommand(DeleteNamespace.class, this::onDeleteNamespace)
            .onCommand(ListNamespaces.class, this::onListNamespaces)
            .build();
    }

    @Override
    public EventHandler<State, NamespacesEvent> eventHandler() {
        return newEventHandlerBuilder()
            .forAnyState()
            .onEvent(CreatedNamespace.class, this::onCreatedNamespace)
            .onEvent(DeletedNamespace.class, this::onDeletedNamespace)
            .build();
    }

    private Effect<NamespacesEvent, State> onCreateNamespace(State state, CreateNamespace create) {
        CreatedNamespace created = CreatedNamespace.apply(create.getName(), create.getExecutor().getUserId(), Instant.now());

        if (state.getNamespaces().contains(create.getName())) {
            create.getReplyTo().tell(created);
            return Effect().none();
        } else {
            return Effect()
                .persist(created)
                .thenRun(() -> create.getReplyTo().tell(created));
        }
    }

    private State onCreatedNamespace(State state, CreatedNamespace created) {
        state.getNamespaces().add(created.getNamespace());
        return state;
    }

    @SuppressWarnings("unused")
    private Effect<NamespacesEvent, State> onDeleteNamespace(State state, DeleteNamespace deleteNamespace) {
        DeletedNamespace deleted = DeletedNamespace.apply(
            deleteNamespace.getName(),
            deleteNamespace.getExecutor().getUserId(),
            Instant.now());

        return Effect()
            .persist(deleted)
            .thenRun(() -> deleteNamespace.getReplyTo().tell(deleted));
    }

    private State onDeletedNamespace(State state, DeletedNamespace deletedNamespace) {
        state.getNamespaces().remove(deletedNamespace.getNamespace());
        return state;
    }

    private Effect<NamespacesEvent, State> onListNamespaces(State state, ListNamespaces list) {
        list.getReplyTo().tell(ListNamespacesResult.apply(state.getNamespaces()));
        return Effect().none();
    }

    @Getter
    @AllArgsConstructor(staticName = "apply")
    public static class State {

        private Set<ResourceName> namespaces;

        public static State apply() {
            return apply(Sets.newHashSet());
        }

    }

}
