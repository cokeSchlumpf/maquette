package maquette.controller.domain.entities.dataset.states;

import java.time.Instant;

import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectFactories;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.protocol.VersionEvent;
import maquette.controller.domain.entities.dataset.protocol.commands.CommitDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PushData;
import maquette.controller.domain.entities.dataset.protocol.events.CommittedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.PushedData;
import maquette.controller.domain.entities.dataset.protocol.queries.GetData;
import maquette.controller.domain.entities.dataset.protocol.queries.GetVersionDetails;
import maquette.controller.domain.entities.dataset.protocol.results.GetDataResult;
import maquette.controller.domain.entities.dataset.protocol.results.GetVersionDetailsResult;
import maquette.controller.domain.ports.DataStorageAdapter;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.Commit;
import maquette.controller.domain.values.dataset.VersionDetails;

@AllArgsConstructor(staticName = "apply")
public final class WorkingVersion implements VersionState {

    private final EffectFactories<VersionEvent, VersionState> effect;

    private final ResourcePath dataset;

    private final DataStorageAdapter store;

    private VersionDetails details;

    @Override
    public Effect<VersionEvent, VersionState> onCommitDatasetVersion(CommitDatasetVersion commit) {
        Commit c = Commit.apply(commit.getExecutor().getUserId(), Instant.now(), commit.getMessage());
        CommittedDatasetVersion committed = CommittedDatasetVersion.apply(
            details.getVersionId(),
            c,
            details.getSchema(),
            details.getRecords());

        return effect
            .persist(committed)
            .thenRun(() -> commit.getReplyTo().tell(committed));
    }

    @Override
    public VersionState onCommittedDatasetVersion(CommittedDatasetVersion committed) {
        return CommittedVersion.apply(effect, dataset, store, details, committed.getCommit());
    }

    @Override
    public Effect<VersionEvent, VersionState> onCreateDatasetVersion(CreateDatasetVersion create) {
        CreatedDatasetVersion created =
            CreatedDatasetVersion.apply(
                dataset, details.getVersionId(), details.getCreatedBy(),
                details.getCreated(), create.getSchema());

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
    public Effect<VersionEvent, VersionState> onGetVersionDetails(GetVersionDetails get) {
        GetVersionDetailsResult result = GetVersionDetailsResult.apply(details);
        get.getReplyTo().tell(result);
        return effect.none();
    }

    @Override
    public Effect<VersionEvent, VersionState> onPushData(PushData push) {
        final Instant modified = Instant.now();

        store.append(details.getVersionId(), push.getRecords());

        VersionDetails newDetails = details
            .withRecords(details.getRecords() + push.getRecords().size())
            .withLastModified(modified)
            .withModifiedBy(push.getExecutor().getUserId());

        PushedData pushed = PushedData.apply(
            dataset, details.getVersionId(), push.getExecutor().getUserId(),
            Instant.now(), newDetails);

        return effect
            .persist(pushed)
            .thenRun(() -> push.getReplyTo().tell(pushed));
    }

    @Override
    public VersionState onPushedData(PushedData data) {
        details = data.getDetails();
        return this;
    }

}
