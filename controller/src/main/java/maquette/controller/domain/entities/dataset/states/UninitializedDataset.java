package maquette.controller.domain.entities.dataset.states;

import java.time.Instant;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import akka.actor.typed.javadsl.ActorContext;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectFactories;
import lombok.AllArgsConstructor;
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
import maquette.controller.domain.entities.dataset.protocol.commands.RevokeDatasetAccess;
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
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.queries.GetData;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.protocol.queries.GetVersionDetails;
import maquette.controller.domain.ports.DataStorageAdapter;
import maquette.controller.domain.values.dataset.DatasetACL;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetDoesNotExistError;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.UserAuthorization;

@AllArgsConstructor(staticName = "apply")
public final class UninitializedDataset implements State {

    private final ActorContext<DatasetMessage> actor;

    private final EffectFactories<DatasetEvent, State> effect;

    private final DataStorageAdapter store;

    private final DeletedDataset deleted;

    public static UninitializedDataset apply(
        ActorContext<DatasetMessage> actor,
        EffectFactories<DatasetEvent, State> effect,
        DataStorageAdapter store) {

        return apply(actor, effect, store,null);
    }

    @Override
    public Effect<DatasetEvent, State> onApproveDatasetAccessRequest(ApproveDatasetAccessRequest approve) {
        approve.getErrorTo().tell(DatasetDoesNotExistError.apply(approve.getDataset()));
        return effect.none();
    }

    @Override
    public State onApprovedDatasetAccessRequest(ApprovedDatasetAccessRequest approved) {
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onChangeDatasetDescription(ChangeDatasetDescription change) {
        change.getErrorTo().tell(DatasetDoesNotExistError.apply(change.getDataset()));
        return effect.none();
    }

    @Override
    public State onChangedDatasetDescription(ChangedDatasetDescription changed) {
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onChangeDatasetGovernance(ChangeDatasetGovernance change) {
        change.getErrorTo().tell(DatasetDoesNotExistError.apply(change.getDataset()));
        return effect.none();
    }

    @Override
    public State onChangedDatasetGovernance(ChangedDatasetGovernance changed) {
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onChangeDatasetPrivacy(ChangeDatasetPrivacy change) {
        change.getErrorTo().tell(DatasetDoesNotExistError.apply(change.getDataset()));
        return effect.none();
    }

    @Override
    public State onChangedDatasetPrivacy(ChangedDatasetPrivacy changed) {
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onChangeOwner(ChangeOwner change) {
        change.getErrorTo().tell(DatasetDoesNotExistError.apply(change.getDataset()));
        return effect.none();
    }

    @Override
    public State onChangedOwner(ChangedOwner changed) {
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onCreateDataset(CreateDataset create) {
        CreatedDataset created = CreatedDataset.apply(
            create.getDataset(),
            create.getDescription(),
            create.isPrivate(),
            create.getGovernance(),
            create.getExecutor().getUserId(),
            Instant.now());

        return effect
            .persist(created)
            .thenRun(() -> create.getReplyTo().tell(created));
    }

    @Override
    public State onCreatedDataset(CreatedDataset created) {
        final GrantedAuthorization granted = GrantedAuthorization.apply(
            created.getCreatedBy(),
            Instant.now(),
            UserAuthorization.apply(created.getCreatedBy()));

        final DatasetDetails details = DatasetDetails.apply(
            created.getDataset(),
            Instant.now(),
            created.getCreatedBy(),
            Instant.now(),
            created.getCreatedBy(),
            Sets.newHashSet(),
            DatasetACL.apply(granted, Sets.newHashSet(), created.isPrivate()),
            created.getDescription(),
            created.getGovernance(),
            Maps.newHashMap());

        return ActiveDataset.apply(actor, effect, store, details);
    }

    @Override
    public Effect<DatasetEvent, State> onCreateDatasetAccessRequest(CreateDatasetAccessRequest create) {
        create.getErrorTo().tell(DatasetDoesNotExistError.apply(create.getDataset()));
        return effect.none();
    }

    @Override
    public State onCreatedDatasetAccessRequest(CreatedDatasetAccessRequest created) {
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onCreateDatasetVersion(CreateDatasetVersion create) {
        return null;
    }

    @Override
    public State onCreatedDatasetVersion(CreatedDatasetVersion created) {
        return null;
    }

    @Override
    public Effect<DatasetEvent, State> onDeleteDataset(DeleteDataset delete) {
        if (deleted == null) {
            DeletedDataset deleted = DeletedDataset.apply(delete.getDataset(), Instant.now(), delete.getExecutor().getUserId());
            delete.getReplyTo().tell(deleted);

            return effect.none();
        } else {
            delete.getReplyTo().tell(deleted);
            return effect.none();
        }
    }

    @Override
    public State onDeletedDataset(DeletedDataset deleted) {
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onGetData(GetData get) {
        get.getErrorTo().tell(DatasetDoesNotExistError.apply(get.getDataset()));
        return effect.none();
    }

    @Override
    public Effect<DatasetEvent, State> onGetDetails(GetDetails get) {
        get.getErrorTo().tell(DatasetDoesNotExistError.apply(get.getDataset()));
        return effect.none();
    }

    @Override
    public Effect<DatasetEvent, State> onGetVersionDetails(GetVersionDetails get) {
        get.getErrorTo().tell(DatasetDoesNotExistError.apply(get.getDataset()));
        return effect.none();
    }

    @Override
    public Effect<DatasetEvent, State> onGrantDatasetAccess(GrantDatasetAccess grant) {
        grant.getErrorTo().tell(DatasetDoesNotExistError.apply(grant.getDataset()));
        return effect.none();
    }

    @Override
    public State onGrantedDatasetAccess(GrantedDatasetAccess granted) {
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onPublishCommittedDatasetVersion(PublishCommittedDatasetVersion publish) {
        publish.getErrorTo().tell(DatasetDoesNotExistError.apply(publish.getDataset()));
        return effect.none();
    }

    @Override
    public Effect<DatasetEvent, State> onPublishDatasetVersion(PublishDatasetVersion publish) {
        return null;
    }

    @Override
    public State onPublishedDatasetVersion(PublishedDatasetVersion published) {
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onPushData(PushData push) {
        return null;
    }

    @Override
    public Effect<DatasetEvent, State> onRevokeDatasetAccess(RevokeDatasetAccess revoke) {
        revoke.getErrorTo().tell(DatasetDoesNotExistError.apply(revoke.getDataset()));
        return effect.none();
    }

    @Override
    public State onRevokedDatasetAccess(RevokedDatasetAccess revoked) {
        return this;
    }

}
