package maquette.controller.domain.entities.namespace;

import akka.actor.typed.javadsl.ActorContext;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.cluster.sharding.typed.javadsl.EventSourcedEntity;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
import maquette.controller.domain.entities.namespace.protocol.NamespaceEvent;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.commands.ChangeNamespacePrivacy;
import maquette.controller.domain.entities.namespace.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.namespace.protocol.commands.CreateNamespace;
import maquette.controller.domain.entities.namespace.protocol.commands.DeleteNamespace;
import maquette.controller.domain.entities.namespace.protocol.commands.GrantNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.commands.RegisterDataset;
import maquette.controller.domain.entities.namespace.protocol.commands.RemoveDataset;
import maquette.controller.domain.entities.namespace.protocol.commands.RevokeNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.events.ChangedNamespacePrivacy;
import maquette.controller.domain.entities.namespace.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.namespace.protocol.events.CreatedNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.DeletedNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.GrantedNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.events.RegisteredDataset;
import maquette.controller.domain.entities.namespace.protocol.events.RemovedDataset;
import maquette.controller.domain.entities.namespace.protocol.events.RevokedNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceDetails;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceInfo;
import maquette.controller.domain.entities.namespace.states.State;
import maquette.controller.domain.entities.namespace.states.UninitializedNamespace;
import maquette.controller.domain.values.core.ResourceName;

public final class Namespace extends EventSourcedEntity<NamespaceMessage, NamespaceEvent, State> {

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
            .onCommand(ChangeNamespacePrivacy.class, State::onChangeNamespacePrivacy)
            .onCommand(ChangeOwner.class, State::onChangeOwner)
            .onCommand(CreateNamespace.class, State::onCreateNamespace)
            .onCommand(DeleteNamespace.class, State::onDeleteNamespace)
            .onCommand(GetNamespaceDetails.class, State::onGetNamespaceDetails)
            .onCommand(GetNamespaceInfo.class, State::onGetNamespaceInfo)
            .onCommand(GrantNamespaceAccess.class, State::onGrantNamespaceAccess)
            .onCommand(RegisterDataset.class, State::onRegisterDataset)
            .onCommand(RemoveDataset.class, State::onRemoveDataset)
            .onCommand(RevokeNamespaceAccess.class, State::onRevokeNamespaceAccess)
            .build();
    }

    @Override
    public EventHandler<State, NamespaceEvent> eventHandler() {
        return newEventHandlerBuilder()
            .forAnyState()
            .onEvent(ChangedNamespacePrivacy.class, State::onChangedNamespacePrivacy)
            .onEvent(ChangedOwner.class, State::onChangedOwner)
            .onEvent(CreatedNamespace.class, State::onCreatedNamespace)
            .onEvent(DeletedNamespace.class, State::onDeletedNamespace)
            .onEvent(GrantedNamespaceAccess.class, State::onGrantedNamespaceAccess)
            .onEvent(RegisteredDataset.class, State::onRegisteredDataset)
            .onEvent(RemovedDataset.class, State::onRemovedDataset)
            .onEvent(RevokedNamespaceAccess.class, State::onRevokedNamespaceAccess)
            .build();
    }

}
