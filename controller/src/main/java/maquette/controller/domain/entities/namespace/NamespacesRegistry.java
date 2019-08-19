package maquette.controller.domain.entities.namespace;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.cluster.typed.SingletonActor;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EventHandler;
import akka.persistence.typed.javadsl.EventSourcedBehavior;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesEvent;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.entities.namespace.protocol.commands.CreateNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.CreatedNamespace;
import maquette.controller.domain.values.core.ResourceName;

public final class NamespacesRegistry extends EventSourcedBehavior<NamespacesMessage, NamespacesEvent, NamespacesRegistry.State> {

    private static final String PERSISTENCE_ID = "resource-registry";

    private final ActorRef<ShardingEnvelope<NamespaceMessage>> namespaceShards;

    private NamespacesRegistry(ActorRef<ShardingEnvelope<NamespaceMessage>> namespaceShards) {
        super(PersistenceId.apply(PERSISTENCE_ID));
        this.namespaceShards = namespaceShards;
    }

    public static SingletonActor<NamespacesMessage> create(ActorRef<ShardingEnvelope<NamespaceMessage>> namespaceShards) {
        Behavior<NamespacesMessage> behavior = Behaviors.setup(actor -> new NamespacesRegistry(namespaceShards));
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
            .build();
    }

    @Override
    public EventHandler<State, NamespacesEvent> eventHandler() {
        return newEventHandlerBuilder()
            .forAnyState()
            .onEvent(CreatedNamespace.class, this::onCreatedNamespace)
            .build();
    }

    private Effect<NamespacesEvent, State> onCreateNamespace(State state, CreateNamespace create) {
        ShardingEnvelope<NamespaceMessage> forwardMessage = ShardingEnvelope.apply(Namespace.createEntityId(create.getName()), create);

        if (state.getNamespaces().contains(create.getName())) {
            namespaceShards.tell(forwardMessage);
            return Effect().none();
        } else {
            CreatedNamespace created = CreatedNamespace.apply(create.getName(), create.getExecutor().getUserId(), Instant.now());

            return Effect()
                .persist(created)
                .thenRun(() -> namespaceShards.tell(forwardMessage));
        }
    }

    private State onCreatedNamespace(State state, CreatedNamespace created) {
        state.getNamespaces().add(created.getNamespace());
        return state;
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
