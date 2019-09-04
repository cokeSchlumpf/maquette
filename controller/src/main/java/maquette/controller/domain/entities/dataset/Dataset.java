package maquette.controller.domain.entities.dataset;

import akka.actor.typed.javadsl.ActorContext;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.cluster.sharding.typed.javadsl.EventSourcedEntity;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.DeleteDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.GrantDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishCommittedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PushData;
import maquette.controller.domain.entities.dataset.protocol.commands.RevokeDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.DeletedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.GrantedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.PublishedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.PushedData;
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.states.State;
import maquette.controller.domain.entities.dataset.states.UninitializedDataset;
import maquette.controller.domain.ports.DataStorageAdapter;
import maquette.controller.domain.values.core.ResourcePath;

public class Dataset extends EventSourcedEntity<DatasetMessage, DatasetEvent, State> {

    public static EntityTypeKey<DatasetMessage> ENTITY_KEY = EntityTypeKey.create(DatasetMessage.class, "dataset");

    private final ActorContext<DatasetMessage> actor;

    private final DataStorageAdapter store;

    private Dataset(
        ActorContext<DatasetMessage> actor,
        DataStorageAdapter store,
        String entityId) {

        super(ENTITY_KEY, entityId);
        this.actor = actor;
        this.store = store;
    }

    public static EventSourcedEntity<DatasetMessage, DatasetEvent, State> create(
        ActorContext<DatasetMessage> actor,
        DataStorageAdapter store,
        ResourcePath id) {

        String entityId = createEntityId(id);
        return new Dataset(actor, store, entityId);
    }

    public static String createEntityId(ResourcePath dataset) {
        return dataset.toString();
    }

    @Override
    public State emptyState() {
        return UninitializedDataset.apply(actor, Effect(), store);
    }

    @Override
    public CommandHandler<DatasetMessage, DatasetEvent, State> commandHandler() {
        return newCommandHandlerBuilder()
            .forAnyState()
            .onCommand(ChangeOwner.class, State::onChangeOwner)
            .onCommand(CreateDataset.class, State::onCreateDataset)
            .onCommand(CreateDatasetVersion.class, State::onCreateDatasetVersion)
            .onCommand(DeleteDataset.class, State::onDeleteDataset)
            .onCommand(GetDetails.class, State::onGetDetails)
            .onCommand(GrantDatasetAccess.class, State::onGrantDatasetAccess)
            .onCommand(PublishCommittedDatasetVersion.class, State::onPublishCommittedDatasetVersion)
            .onCommand(PublishDatasetVersion.class, State::onPublishDatasetVersion)
            .onCommand(PushData.class, State::onPushData)
            .onCommand(RevokeDatasetAccess.class, State::onRevokeDatasetAccess)
            .build();
    }

    @Override
    public EventHandler<State, DatasetEvent> eventHandler() {
        return newEventHandlerBuilder()
            .forAnyState()
            .onEvent(ChangedOwner.class, State::onChangedOwner)
            .onEvent(CreatedDataset.class, State::onCreatedDataset)
            .onEvent(CreatedDatasetVersion.class, State::onCreatedDatasetVersion)
            .onEvent(DeletedDataset.class, State::onDeletedDataset)
            .onEvent(GrantedDatasetAccess.class, State::onGrantedDatasetAccess)
            .onEvent(PublishedDatasetVersion.class, State::onPublishedDatasetVersion)
            .onEvent(PushedData.class, State::onPushedData)
            .onEvent(RevokedDatasetAccess.class, State::onRevokedDatasetAccess)
            .build();
    }

}
