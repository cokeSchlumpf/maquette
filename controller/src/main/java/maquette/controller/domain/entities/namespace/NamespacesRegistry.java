package maquette.controller.domain.entities.namespace;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.typed.SingletonActor;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
import akka.persistence.typed.javadsl.EventSourcedBehavior;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.namespace.protocol.NamespacesEvent;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;

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
            .build();
    }

    @Override
    public EventHandler<State, NamespacesEvent> eventHandler() {
        return newEventHandlerBuilder()
            .build();
    }

    @AllArgsConstructor(staticName = "apply")
    public static class State {

    }

}
