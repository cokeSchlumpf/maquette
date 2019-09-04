package maquette.controller.domain.entities.dataset.states;

import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectFactories;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.protocol.VersionEvent;
import maquette.controller.domain.entities.dataset.protocol.commands.CommitDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PushData;
import maquette.controller.domain.entities.dataset.protocol.events.CommittedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.PushedData;
import maquette.controller.domain.entities.dataset.protocol.queries.GetData;
import maquette.controller.domain.entities.dataset.protocol.results.GetDataResult;
import maquette.controller.domain.ports.DataStorageAdapter;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.AlreadyCommittedError;
import maquette.controller.domain.values.dataset.Commit;
import maquette.controller.domain.values.dataset.VersionDetails;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommittedVersion implements VersionState {

    private final EffectFactories<VersionEvent, VersionState> effect;

    private final ResourcePath dataset;

    private final DataStorageAdapter store;

    private final VersionDetails details;

    private final Commit commit;

    public static CommittedVersion apply(
        EffectFactories<VersionEvent, VersionState> effect,
        ResourcePath dataset,
        DataStorageAdapter store,
        VersionDetails details,
        Commit commit) {

        VersionDetails newDetails = details
            .withCommit(commit)
            .withLastModified(commit.getCommittedAt())
            .withModifiedBy(commit.getCommittedBy());

        return new CommittedVersion(effect, dataset, store, newDetails, commit);
    }

    @Override
    public Effect<VersionEvent, VersionState> onCommitDatasetVersion(CommitDatasetVersion commit) {
        CommittedDatasetVersion committed = CommittedDatasetVersion.apply(
            this.details.getVersionId(),
            this.commit);

        commit.getReplyTo().tell(committed);
        return effect.none();
    }

    @Override
    public VersionState onCommittedDatasetVersion(CommittedDatasetVersion committed) {
        return this;
    }

    @Override
    public Effect<VersionEvent, VersionState> onCreateDatasetVersion(CreateDatasetVersion create) {
        CreatedDatasetVersion created =
            CreatedDatasetVersion.apply(dataset, details.getVersionId(), details.getCreatedBy(), details.getCreated());

        create.getReplyTo().tell(created);
        return effect.none();
    }

    @Override
    public VersionState onCreatedDatasetVersion(CreatedDatasetVersion created) {
        return this;
    }

    @Override
    public Effect<VersionEvent, VersionState> onGetData(GetData get) {
        GetDataResult result = GetDataResult.apply(store.get(details.getVersionId()));
        get.getReplyTo().tell(result);
        return effect.none();
    }

    @Override
    public Effect<VersionEvent, VersionState> onPushData(PushData push) {
        push.getErrorTo().tell(AlreadyCommittedError.apply());
        return effect.none();
    }

    @Override
    public VersionState onPushedData(PushedData data) {
        return this;
    }

}
