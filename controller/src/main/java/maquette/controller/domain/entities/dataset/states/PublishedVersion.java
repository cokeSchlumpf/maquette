package maquette.controller.domain.entities.dataset.states;

import akka.persistence.typed.javadsl.Effect;
import maquette.controller.domain.entities.dataset.protocol.VersionEvent;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PushData;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.PublishedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.PushedData;
import maquette.controller.domain.entities.dataset.protocol.queries.GetData;

public final class PublishedVersion implements VersionState {

    @Override
    public Effect<VersionEvent, VersionState> onCreateDatasetVersion(CreateDatasetVersion create) {
        return null;
    }

    @Override
    public VersionState onCreatedDatasetVersion(CreatedDatasetVersion created) {
        return null;
    }

    @Override
    public Effect<VersionEvent, VersionState> onGetData(GetData get) {
        return null;
    }

    @Override
    public Effect<VersionEvent, VersionState> onPushData(PushData push) {
        return null;
    }

    @Override
    public VersionState onPushedData(PushedData data) {
        return null;
    }

    @Override
    public Effect<VersionEvent, VersionState> onPublishDatasetVersion(PublishDatasetVersion publish) {
        return null;
    }

    @Override
    public VersionState onPublishedDatasetVersion(PublishedDatasetVersion published) {
        return null;
    }

}
