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

public interface VersionState {

    Effect<VersionEvent, VersionState> onCreateDatasetVersion(CreateDatasetVersion create);

    VersionState onCreatedDatasetVersion(CreatedDatasetVersion created);

    Effect<VersionEvent, VersionState> onGetData(GetData get);

    Effect<VersionEvent, VersionState> onPushData(PushData push);

    VersionState onPushedData(PushedData data);

    Effect<VersionEvent, VersionState> onPublishDatasetVersion(PublishDatasetVersion publish);

    VersionState onPublishedDatasetVersion(PublishedDatasetVersion published);

}
