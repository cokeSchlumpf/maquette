package maquette.controller.domain.entities.dataset;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
import akka.persistence.typed.javadsl.EventSourcedBehavior;
import maquette.controller.domain.entities.dataset.protocol.VersionEvent;
import maquette.controller.domain.entities.dataset.protocol.VersionMessage;
import maquette.controller.domain.entities.dataset.protocol.commands.CommitDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PushData;
import maquette.controller.domain.entities.dataset.protocol.events.CommittedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.PushedData;
import maquette.controller.domain.entities.dataset.protocol.queries.GetData;
import maquette.controller.domain.entities.dataset.states.VersionState;
import maquette.controller.domain.entities.dataset.states.WorkingVersion;
import maquette.controller.domain.ports.DataStorageAdapter;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.dataset.VersionDetails;

public class Version extends EventSourcedBehavior<VersionMessage, VersionEvent, VersionState> {

    private final VersionDetails initialDetails;

    private final ResourcePath dataset;

    private final DataStorageAdapter store;

    private Version(
        PersistenceId persistenceId, ResourcePath dataset, VersionDetails details,
        DataStorageAdapter store) {

        super(persistenceId);
        this.dataset = dataset;
        this.initialDetails = details;
        this.store = store;
    }

    public static Behavior<VersionMessage> create(
        CreatedDatasetVersion created, UID versionid, DataStorageAdapter store) {
        VersionDetails details = VersionDetails.apply(
            created.versionId,
            created.createdAt,
            created.createdBy,
            created.createdAt,
            created.createdBy,
            0L);

        PersistenceId id = PersistenceId.apply(versionid.getValue());

        return Behaviors.setup(ctx -> new Version(id, created.dataset, details, store));
    }

    @Override
    public VersionState emptyState() {
        return WorkingVersion.apply(Effect(), dataset, store, initialDetails);
    }

    @Override
    public CommandHandler<VersionMessage, VersionEvent, VersionState> commandHandler() {
        return newCommandHandlerBuilder()
            .forAnyState()
            .onCommand(CommitDatasetVersion.class, VersionState::onCommitDatasetVersion)
            .onCommand(CreateDatasetVersion.class, VersionState::onCreateDatasetVersion)
            .onCommand(GetData.class, VersionState::onGetData)
            .onCommand(PushData.class, VersionState::onPushData)
            .build();
    }

    @Override
    public EventHandler<VersionState, VersionEvent> eventHandler() {
        return newEventHandlerBuilder()
            .forAnyState()
            .onEvent(CommittedDatasetVersion.class, VersionState::onCommittedDatasetVersion)
            .onEvent(CreatedDatasetVersion.class, VersionState::onCreatedDatasetVersion)
            .onEvent(PushedData.class, VersionState::onPushedData)
            .build();
    }

}
