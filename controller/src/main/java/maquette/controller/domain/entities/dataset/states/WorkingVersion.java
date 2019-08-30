package maquette.controller.domain.entities.dataset.states;

import akka.actor.typed.javadsl.ActorContext;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectFactories;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.protocol.VersionEvent;
import maquette.controller.domain.entities.dataset.protocol.VersionMessage;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PushData;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.PublishedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.PushedData;
import maquette.controller.domain.entities.dataset.protocol.queries.GetData;
import maquette.controller.domain.values.dataset.VersionDetails;

@AllArgsConstructor(staticName = "apply")
public final class WorkingVersion implements VersionState {

    private final ActorContext<VersionMessage> actor;

    private final EffectFactories<VersionEvent, VersionState> effect;

    private final VersionDetails details;

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
