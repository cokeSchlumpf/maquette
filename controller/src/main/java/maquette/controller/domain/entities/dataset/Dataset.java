package maquette.controller.domain.entities.dataset;

import akka.actor.typed.javadsl.ActorContext;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.cluster.sharding.typed.javadsl.EventSourcedEntity;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.commands.ApproveDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeDatasetDescription;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeDatasetGovernance;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeDatasetPrivacy;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.DeleteDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.GrantDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishCommittedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PushData;
import maquette.controller.domain.entities.dataset.protocol.commands.RejectDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.commands.RevokeDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.commands.RevokeDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.events.ApprovedDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedDatasetDescription;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedDatasetGovernance;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedDatasetPrivacy;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.DeletedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.GrantedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.PublishedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.RejectedDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.queries.GetData;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.protocol.queries.GetVersionDetails;
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
            .onCommand(ApproveDatasetAccessRequest.class, State::onApproveDatasetAccessRequest)
            .onCommand(ChangeDatasetDescription.class, State::onChangeDatasetDescription)
            .onCommand(ChangeDatasetGovernance.class, State::onChangeDatasetGovernance)
            .onCommand(ChangeDatasetPrivacy.class, State::onChangeDatasetPrivacy)
            .onCommand(ChangeOwner.class, State::onChangeOwner)
            .onCommand(CreateDataset.class, State::onCreateDataset)
            .onCommand(CreateDatasetAccessRequest.class, State::onCreateDatasetAccessRequest)
            .onCommand(CreateDatasetVersion.class, State::onCreateDatasetVersion)
            .onCommand(DeleteDataset.class, State::onDeleteDataset)
            .onCommand(GetData.class, State::onGetData)
            .onCommand(GetDetails.class, State::onGetDetails)
            .onCommand(GetVersionDetails.class, State::onGetVersionDetails)
            .onCommand(GrantDatasetAccess.class, State::onGrantDatasetAccess)
            .onCommand(PublishCommittedDatasetVersion.class, State::onPublishCommittedDatasetVersion)
            .onCommand(PublishDatasetVersion.class, State::onPublishDatasetVersion)
            .onCommand(PushData.class, State::onPushData)
            .onCommand(RejectDatasetAccessRequest.class, State::onRejectDatasetAccessRequest)
            .onCommand(RevokeDatasetAccess.class, State::onRevokeDatasetAccess)
            .onCommand(RevokeDatasetAccessRequest.class, State::onRevokeDatasetAccessRequest)
            .build();
    }

    @Override
    public EventHandler<State, DatasetEvent> eventHandler() {
        return newEventHandlerBuilder()
            .forAnyState()
            .onEvent(ApprovedDatasetAccessRequest.class, State::onApprovedDatasetAccessRequest)
            .onEvent(ChangedDatasetDescription.class, State::onChangedDatasetDescription)
            .onEvent(ChangedDatasetGovernance.class, State::onChangedDatasetGovernance)
            .onEvent(ChangedDatasetPrivacy.class, State::onChangedDatasetPrivacy)
            .onEvent(ChangedOwner.class, State::onChangedOwner)
            .onEvent(CreatedDataset.class, State::onCreatedDataset)
            .onEvent(CreatedDatasetAccessRequest.class, State::onCreatedDatasetAccessRequest)
            .onEvent(CreatedDatasetVersion.class, State::onCreatedDatasetVersion)
            .onEvent(DeletedDataset.class, State::onDeletedDataset)
            .onEvent(GrantedDatasetAccess.class, State::onGrantedDatasetAccess)
            .onEvent(PublishedDatasetVersion.class, State::onPublishedDatasetVersion)
            .onEvent(RejectedDatasetAccessRequest.class, State::onRejectedDatasetAccessRequest)
            .onEvent(RevokedDatasetAccess.class, State::onRevokedDatasetAccess)
            .onEvent(RevokedDatasetAccessRequest.class, State::onRevokedDatasetAccessRequest)
            .build();
    }

}
