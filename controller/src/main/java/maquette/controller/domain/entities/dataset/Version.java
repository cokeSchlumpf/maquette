package maquette.controller.domain.entities.dataset;

import akka.actor.typed.Behavior;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
import akka.persistence.typed.javadsl.EventSourcedBehavior;
import maquette.controller.domain.entities.dataset.protocol.VersionEvent;
import maquette.controller.domain.entities.dataset.protocol.VersionMessage;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PushData;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.PublishedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.PushedData;
import maquette.controller.domain.entities.dataset.protocol.queries.GetData;
import maquette.controller.domain.entities.dataset.states.VersionState;

public class Version extends EventSourcedBehavior<VersionMessage, VersionEvent, VersionState> {

    private Version(PersistenceId persistenceId) {
        super(persistenceId);
    }

    public static Behavior<VersionMessage> create() {
        return null;
    }

    @Override
    public VersionState emptyState() {
        return null;
    }

    @Override
    public CommandHandler<VersionMessage, VersionEvent, VersionState> commandHandler() {
        return newCommandHandlerBuilder()
            .forAnyState()
            .onCommand(CreateDatasetVersion.class, VersionState::onCreateDatasetVersion)
            .onCommand(GetData.class, VersionState::onGetData)
            .onCommand(PublishDatasetVersion.class, VersionState::onPublishDatasetVersion)
            .onCommand(PushData.class, VersionState::onPushData)
            .build();
    }

    @Override
    public EventHandler<VersionState, VersionEvent> eventHandler() {
        return newEventHandlerBuilder()
            .forAnyState()
            .onEvent(CreatedDatasetVersion.class, VersionState::onCreatedDatasetVersion)
            .onEvent(PublishedDatasetVersion.class, VersionState::onPublishedDatasetVersion)
            .onEvent(PushedData.class, VersionState::onPushedData)
            .build();
    }

}
