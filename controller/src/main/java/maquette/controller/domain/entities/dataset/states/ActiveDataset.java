package maquette.controller.domain.entities.dataset.states;

import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.ActorContext;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectFactories;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.Version;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.VersionMessage;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeDatasetPrivacy;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.DeleteDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.GrantDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishCommittedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PushData;
import maquette.controller.domain.entities.dataset.protocol.commands.RevokeDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedDatasetPrivacy;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.DeletedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.GrantedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.PublishedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.queries.GetData;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.protocol.queries.GetVersionDetails;
import maquette.controller.domain.entities.dataset.services.CollectDetails;
import maquette.controller.domain.entities.dataset.services.PublishVersion;
import maquette.controller.domain.ports.DataStorageAdapter;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.dataset.DatasetACL;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetGrant;
import maquette.controller.domain.values.dataset.VersionDoesNotExistError;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.dataset.VersionTagInfo;
import maquette.controller.domain.values.iam.GrantedAuthorization;

@AllArgsConstructor(staticName = "apply")
public final class ActiveDataset implements State {

    private final ActorContext<DatasetMessage> actor;

    private final EffectFactories<DatasetEvent, State> effect;

    private final DataStorageAdapter store;

    private DatasetDetails details;

    private final Map<UID, ActorRef<VersionMessage>> versions;

    private final Map<VersionTag, VersionTagInfo> publishedVersions;

    public static ActiveDataset apply(
        ActorContext<DatasetMessage> actor,
        EffectFactories<DatasetEvent, State> effect,
        DataStorageAdapter store,
        DatasetDetails details) {

        return apply(actor, effect, store, details, Maps.newHashMap(), Maps.newHashMap());
    }

    @Override
    public Effect<DatasetEvent, State> onChangeDatasetPrivacy(ChangeDatasetPrivacy change) {
        ChangedDatasetPrivacy changed =
            ChangedDatasetPrivacy.apply(details.getDataset(), change.isPrivate(), change.getExecutor().getUserId(), Instant.now());

        if (Boolean.valueOf(details.getAcl().isPrivate()).equals(change.isPrivate())) {
            change.getReplyTo().tell(changed);
            return effect.none();
        } else {
            return effect
                .persist(changed)
                .thenRun(() -> change.getReplyTo().tell(changed));
        }
    }

    @Override
    public State onChangedDatasetPrivacy(ChangedDatasetPrivacy changed) {
        DatasetACL acl$new = this.details.getAcl().withPrivacy(changed.isPrivate());

        this.details = this.details
            .withAcl(acl$new)
            .withModified(changed.getChangedAt())
            .withModifiedBy(changed.getChangedBy());

        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onChangeOwner(ChangeOwner change) {
        if (change.getOwner().equals(details.getAcl().getOwner().getAuthorization())) {
            ChangedOwner changed = ChangedOwner.apply(details.getDataset(), details.getAcl().getOwner());
            change.getReplyTo().tell(changed);
            return effect.none();
        } else {
            GrantedAuthorization granted = GrantedAuthorization.apply(
                change.getExecutor().getUserId(),
                Instant.now(),
                change.getOwner());

            ChangedOwner
                changedOwner = ChangedOwner.apply(details.getDataset(), granted);

            return effect
                .persist(changedOwner)
                .thenRun(() -> change.getReplyTo().tell(changedOwner));
        }
    }

    @Override
    public State onChangedOwner(ChangedOwner changed) {
        this.details = this.details
            .withAcl(this.details.getAcl().withOwner(changed.getNewOwner()))
            .withModified(changed.getNewOwner().getAt())
            .withModifiedBy(changed.getNewOwner().getBy());

        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onCreateDataset(CreateDataset create) {
        CreatedDataset created = CreatedDataset.apply(details.getDataset(), details.getAcl().isPrivate(), details.getCreatedBy(), details.getCreated());
        create.getReplyTo().tell(created);
        return effect.none();
    }

    @Override
    public State onCreatedDataset(CreatedDataset created) {
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onCreateDatasetVersion(CreateDatasetVersion create) {
        CreatedDatasetVersion created = CreatedDatasetVersion.apply(
            details.getDataset(),
            UID.apply(),
            create.getExecutor().getUserId(),
            Instant.now(),
            create.getSchema());

        return effect
            .persist(created)
            .thenRun(() -> create.getReplyTo().tell(created));
    }

    @Override
    public State onCreatedDatasetVersion(CreatedDatasetVersion created) {
        if (!actor.getChild(created.versionId.getValue()).isPresent()) {
            ActorRef<VersionMessage> version = actor.spawn(
                Version.create(created, created.versionId, store),
                created.versionId.getValue());

            versions.put(created.versionId, version);
        }

        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onDeleteDataset(DeleteDataset delete) {
        DeletedDataset deleted = DeletedDataset.apply(details.getDataset(), Instant.now(), delete.getExecutor().getUserId());

        return effect
            .persist(deleted)
            .thenRun(() -> delete.getReplyTo().tell(deleted));
    }

    @Override
    public State onDeletedDataset(DeletedDataset deleted) {
        // TODO: Also delete all versions?
        return UninitializedDataset.apply(actor, effect, store, deleted);
    }

    @Override
    public Effect<DatasetEvent, State> onGetData(GetData get) {
        if (versions.containsKey(get.getVersionId())) {
            versions.get(get.getVersionId()).tell(get);
        } else {
            get.getErrorTo().tell(VersionDoesNotExistError.apply(details.getDataset(), get.getVersionId()));
        }

        return effect.none();
    }

    @Override
    public Effect<DatasetEvent, State> onGetDetails(GetDetails get) {
        actor.spawnAnonymous(CollectDetails.create(
            Lists.newArrayList(this.publishedVersions.values()),
            ImmutableMap.copyOf(versions),
            get,
            details));

        return effect.none();
    }

    @Override
    public Effect<DatasetEvent, State> onGetVersionDetails(GetVersionDetails get) {
        if (versions.containsKey(get.getVersionId())) {
            versions.get(get.getVersionId()).tell(get);
        } else {
            get.getErrorTo().tell(VersionDoesNotExistError.apply(details.getDataset(), get.getVersionId()));
        }

        return effect.none();
    }

    @Override
    public Effect<DatasetEvent, State> onGrantDatasetAccess(GrantDatasetAccess grant) {
        Optional<DatasetGrant> existing = details.getAcl().findGrant(grant.getGrantFor(), grant.getGrant());

        if (existing.isPresent()) {
            GrantedDatasetAccess granted = GrantedDatasetAccess.apply(
                details.getDataset(),
                grant.getGrant(),
                existing.get().getAuthorization());

            grant.getReplyTo().tell(granted);

            return effect.none();
        } else {
            GrantedAuthorization grantedAuthorization = GrantedAuthorization.apply(
                grant.getExecutor().getUserId(),
                Instant.now(),
                grant.getGrantFor());

            GrantedDatasetAccess granted = GrantedDatasetAccess.apply(
                details.getDataset(),
                grant.getGrant(),
                grantedAuthorization);

            return effect
                .persist(granted)
                .thenRun(() -> grant.getReplyTo().tell(granted));
        }
    }

    @Override
    public State onGrantedDatasetAccess(GrantedDatasetAccess granted) {
        DatasetACL acl = details.getAcl().withGrant(granted.getGrantedFor(), granted.getGranted());
        details = details.withAcl(acl);
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onPublishCommittedDatasetVersion(PublishCommittedDatasetVersion publish) {
        VersionTag version = publishedVersions
            .keySet()
            .stream()
            .max(Comparator.naturalOrder())
            .map(tag -> publishedVersions.get(tag).nextVersion(publish.getSchema()))
            .orElse(VersionTag.apply(1, 0, 0));

        VersionTagInfo info = VersionTagInfo.apply(publish.getVersionId(), version, publish.getSchema());

        PublishedDatasetVersion published = PublishedDatasetVersion.apply(details.getDataset(), publish.getCommit(), info);

        return effect
            .persist(published)
            .thenRun(() -> publish.getReplyTo().tell(published));
    }

    @Override
    public Effect<DatasetEvent, State> onPublishDatasetVersion(PublishDatasetVersion publish) {
        if (versions.containsKey(publish.getVersionId())) {
            actor.spawnAnonymous(PublishVersion.create(actor.getSelf(), versions.get(publish.getVersionId()), publish));
        } else {
            publish.getErrorTo().tell(VersionDoesNotExistError.apply(details.getDataset(), publish.getVersionId()));
        }

        return effect.none();
    }

    @Override
    public State onPublishedDatasetVersion(PublishedDatasetVersion published) {
        this.publishedVersions.put(published.getVersion().getVersion(), published.getVersion());
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onPushData(PushData push) {
        if (versions.containsKey(push.getVersionId())) {
            versions.get(push.getVersionId()).tell(push);
        } else {
            push.getErrorTo().tell(VersionDoesNotExistError.apply(details.getDataset(), push.getVersionId()));
        }

        return effect.none();
    }

    @Override
    public Effect<DatasetEvent, State> onRevokeDatasetAccess(RevokeDatasetAccess revoke) {
        Optional<DatasetGrant> existing = details.getAcl().findGrant(revoke.getRevokeFrom(), revoke.getRevoke());

        if (existing.isPresent()) {
            RevokedDatasetAccess revoked = RevokedDatasetAccess.apply(
                details.getDataset(),
                revoke.getRevoke(),
                Instant.now(),
                revoke.getExecutor().getUserId(),
                existing.get().getAuthorization());

            return effect
                .persist(revoked)
                .thenRun(() -> revoke.getReplyTo().tell(revoked));
        } else {
            GrantedAuthorization grantedAuthorization = GrantedAuthorization.apply(
                revoke.getExecutor().getUserId(),
                Instant.now(),
                revoke.getRevokeFrom());

            RevokedDatasetAccess revoked = RevokedDatasetAccess.apply(
                details.getDataset(),
                revoke.getRevoke(),
                Instant.now(),
                revoke.getExecutor().getUserId(),
                grantedAuthorization);

            revoke.getReplyTo().tell(revoked);

            return effect
                .none();

        }
    }

    @Override
    public State onRevokedDatasetAccess(RevokedDatasetAccess revoked) {
        DatasetACL acl = details.getAcl().withoutGrant(revoked.getRevokedFrom().getAuthorization(), revoked.getRevoked());
        details = details.withAcl(acl);
        return this;
    }

}
