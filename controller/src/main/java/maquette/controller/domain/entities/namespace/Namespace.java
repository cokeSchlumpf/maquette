package maquette.controller.domain.entities.namespace;

import akka.actor.typed.javadsl.ActorContext;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.cluster.sharding.typed.javadsl.EventSourcedEntity;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.namespace.protocol.NamespaceEvent;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.UID;

public class Namespace extends EventSourcedEntity<NamespaceMessage, NamespaceEvent, Namespace.State> {

    public static EntityTypeKey<NamespaceMessage> ENTITY_KEY = EntityTypeKey.create(NamespaceMessage.class, "namespace");

    private final State initialState;

    private final ActorContext<NamespaceMessage> actor;

    private Namespace(String entityId, ActorContext<NamespaceMessage> actor, State initialState) {
        super(ENTITY_KEY, entityId);
        this.actor = actor;
        this.initialState = initialState;
    }

    public static EventSourcedEntity<NamespaceMessage, NamespaceEvent, Namespace.State> create(
        ActorContext<NamespaceMessage> actor, ResourceName name) {

        String entityId = createEntityId(name);
        State initialState = State.apply(name);

        return new Namespace(entityId, actor, initialState);
    }

    public static String createEntityId(ResourceName namespaceName) {
        return namespaceName.getValue();
    }

    @Override
    public State emptyState() {
        return initialState;
    }

    @Override
    public CommandHandler<NamespaceMessage, NamespaceEvent, State> commandHandler() {
        return newCommandHandlerBuilder().build();
    }

    @Override
    public EventHandler<State, NamespaceEvent> eventHandler() {
        return newEventHandlerBuilder().build();
    }

    @AllArgsConstructor(staticName = "apply")
    public static final class State {

        private final ResourceName name;

    }

}
