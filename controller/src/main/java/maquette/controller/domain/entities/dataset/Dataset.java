package maquette.controller.domain.entities.dataset;

import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.cluster.sharding.typed.javadsl.EventSourcedEntity;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.states.State;
import maquette.controller.domain.values.core.UID;

public class Dataset extends EventSourcedEntity<DatasetMessage, DatasetEvent, State> {

    public static EntityTypeKey<DatasetMessage> ENTITY_KEY = EntityTypeKey.create(DatasetMessage.class, "dataset");

    private Dataset(UID uid) {
        super(ENTITY_KEY, uid.getValue());
    }

    @Override
    public State emptyState() {
        return null;
    }

    @Override
    public CommandHandler<DatasetMessage, DatasetEvent, State> commandHandler() {
        return null;
    }

    @Override
    public EventHandler<State, DatasetEvent> eventHandler() {
        return null;
    }

}
