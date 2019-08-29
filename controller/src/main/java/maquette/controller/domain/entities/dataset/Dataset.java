package maquette.controller.domain.entities.dataset;

import akka.actor.typed.javadsl.ActorContext;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.cluster.sharding.typed.javadsl.EventSourcedEntity;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.DeleteDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.GrantDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.commands.RevokeDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.DeletedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.GrantedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.states.State;
import maquette.controller.domain.entities.dataset.states.UninitializedDataset;
import maquette.controller.domain.values.core.ResourcePath;

public class Dataset extends EventSourcedEntity<DatasetMessage, DatasetEvent, State> {

    public static EntityTypeKey<DatasetMessage> ENTITY_KEY = EntityTypeKey.create(DatasetMessage.class, "dataset");

    private final ActorContext<DatasetMessage> actor;

    private Dataset(
        ActorContext<DatasetMessage> actor,
        String entityId) {

        super(ENTITY_KEY, entityId);
        this.actor = actor;
    }

    public static EventSourcedEntity<DatasetMessage, DatasetEvent, State> create(
        ActorContext<DatasetMessage> actor,
        ResourcePath id) {

        String entityId = createEntityId(id);
        return new Dataset(actor, entityId);
    }

    public static String createEntityId(ResourcePath dataset) {
        return dataset.toString();
    }

    @Override
    public State emptyState() {
        return UninitializedDataset.apply(actor, Effect());
    }

    @Override
    public CommandHandler<DatasetMessage, DatasetEvent, State> commandHandler() {
        return newCommandHandlerBuilder()
            .forAnyState()
            .onCommand(CreateDataset.class, State::onCreateDataset)
            .onCommand(DeleteDataset.class, State::onDeleteDataset)
            .onCommand(GetDetails.class, State::onGetDetails)
            .onCommand(GrantDatasetAccess.class, State::onGrantDatasetAccess)
            .onCommand(RevokeDatasetAccess.class, State::onRevokeDatasetAccess)
            .build();
    }

    @Override
    public EventHandler<State, DatasetEvent> eventHandler() {
        return newEventHandlerBuilder()
            .forAnyState()
            .onEvent(CreatedDataset.class, State::onCreatedDataset)
            .onEvent(DeletedDataset.class, State::onDeletedDataset)
            .onEvent(GrantedDatasetAccess.class, State::onGrantedDatasetAccess)
            .onEvent(RevokedDatasetAccess.class, State::onRevokedDatasetAccess)
            .build();
    }

}
