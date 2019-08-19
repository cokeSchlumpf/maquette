package maquette.controller.domain.entities.namespace;

import akka.actor.typed.javadsl.ActorContext;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.cluster.sharding.typed.javadsl.EventSourcedEntity;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EventHandler;
import maquette.controller.domain.entities.namespace.protocol.NamespaceEvent;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.commands.CreateNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.CreatedNamespace;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceInfo;
import maquette.controller.domain.entities.namespace.states.State;
import maquette.controller.domain.entities.namespace.states.UninitializedNamespace;
import maquette.controller.domain.values.core.ResourceName;

public class Namespace extends EventSourcedEntity<NamespaceMessage, NamespaceEvent, State> {

    public static EntityTypeKey<NamespaceMessage> ENTITY_KEY = EntityTypeKey.create(NamespaceMessage.class, "namespace");

    private final ActorContext<NamespaceMessage> actor;

    private Namespace(String entityId, ActorContext<NamespaceMessage> actor) {
        super(ENTITY_KEY, entityId);
        this.actor = actor;
    }

    public static EventSourcedEntity<NamespaceMessage, NamespaceEvent, State> create(
        ActorContext<NamespaceMessage> actor, ResourceName name) {

        String entityId = createEntityId(name);
        return new Namespace(entityId, actor);
    }

    public static String createEntityId(ResourceName namespaceName) {
        return namespaceName.getValue();
    }

    @Override
    public State emptyState() {
        return UninitializedNamespace.apply(actor, Effect());
    }

    @Override
    public CommandHandler<NamespaceMessage, NamespaceEvent, State> commandHandler() {
        return newCommandHandlerBuilder()
            .forAnyState()
            .onCommand(CreateNamespace.class, State::onCreateNamespace)
            .onCommand(GetNamespaceInfo.class, State::onGetNamespaceInfo)
            .build();
    }

    @Override
    public EventHandler<State, NamespaceEvent> eventHandler() {
        return newEventHandlerBuilder()
            .forAnyState()
            .onEvent(CreatedNamespace.class, State::onCreatedNamespace)
            .build();
    }

}
