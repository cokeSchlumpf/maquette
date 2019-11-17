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
import maquette.controller.domain.entities.dataset.protocol.commands.RejectDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.commands.RevokeDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.commands.RevokeDatasetAccessRequest;
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
import maquette.controller.domain.entities.dataset.protocol.events.RejectedDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.queries.GetAllVersions;
import maquette.controller.domain.entities.dataset.protocol.queries.GetData;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.protocol.queries.GetVersionDetails;
import maquette.controller.domain.entities.dataset.protocol.results.GetDetailsResult;
import maquette.controller.domain.entities.dataset.services.CollectAllVersions;
import maquette.controller.domain.entities.dataset.services.PublishVersion;
import maquette.controller.domain.ports.DataStorageAdapter;
import maquette.controller.domain.values.core.Executed;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.governance.AccessRequest;
import maquette.controller.domain.values.dataset.DatasetACL;
import maquette.controller.domain.values.dataset.DatasetAccessRequestDoesNotExistError;
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

    public static ActiveDataset apply(
        ActorContext<DatasetMessage> actor,
        EffectFactories<DatasetEvent, State> effect,
        DataStorageAdapter store,
        DatasetDetails details) {

        return apply(actor, effect, store, details, Maps.newHashMap());
    }

    @Override
    public Effect<DatasetEvent, State> onApproveDatasetAccessRequest(ApproveDatasetAccessRequest approve) {
        Optional<DatasetGrant> optGrant = details.getAcl().findGrantById(approve.getId());

        if (optGrant.isPresent()) {
            return optGrant
                .get()
                .approve(approve.getExecutor().getUserId(), Instant.now(), approve.getComment())
                .map(
                    grant$new -> {
                        ApprovedDatasetAccessRequest approvedRequest = ApprovedDatasetAccessRequest.apply(grant$new);

                        return effect
                            .persist(approvedRequest)
                            .thenRun(() -> approve.getReplyTo().tell(approvedRequest));
                    },
                    errorMessage -> {
                        approve.getErrorTo().tell(errorMessage);
                        return effect.none();
                    });
        } else {
            approve.getErrorTo().tell(DatasetAccessRequestDoesNotExistError.apply(details.getDataset(), approve.getId()));
            return effect.none();
        }
    }

    @Override
    public State onApprovedDatasetAccessRequest(ApprovedDatasetAccessRequest approved) {
        DatasetACL acl = details.getAcl().withGrant(approved.getGrant());
        details = details.withAcl(acl);
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onChangeDatasetDescription(ChangeDatasetDescription change) {
        ChangedDatasetDescription changed = ChangedDatasetDescription.apply(
            change.getDataset(), change.getDescription(), change.getExecutor().getUserId(), Instant.now());

        if (details.getDescription().isPresent() && details.getDescription().get().equals(change.getDescription())) {
            change.getReplyTo().tell(changed);
            return effect.none();
        } else {
            return effect
                .persist(changed)
                .thenRun(() -> change.getReplyTo().tell(changed));
        }
    }

    @Override
    public State onChangedDatasetDescription(ChangedDatasetDescription changed) {
        this.details = details
            .withDescription(changed.getDescription())
            .withModifiedBy(changed.getChangedBy())
            .withModified(changed.getChangedAt());

        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onChangeDatasetGovernance(ChangeDatasetGovernance change) {
        ChangedDatasetGovernance changed = ChangedDatasetGovernance.apply(
            change.getDataset(), change.getGovernance(), change.getExecutor().getUserId(), Instant.now());

        if (details.getGovernance().equals(changed.getGovernance())) {
            change.getReplyTo().tell(changed);
            return effect.none();
        } else {
            return effect
                .persist(changed)
                .thenRun(() -> change.getReplyTo().tell(changed));
        }
    }

    @Override
    public State onChangedDatasetGovernance(ChangedDatasetGovernance changed) {
        this.details = details
            .withGovernance(changed.getGovernance())
            .withModifiedBy(changed.getChangedBy())
            .withModified(changed.getChangedAt());

        return this;
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
        CreatedDataset created = CreatedDataset.apply(
            details.getDataset(), details.getDescription().orElse(Markdown.apply()),
            details.getAcl().isPrivate(), details.getGovernance(),
            details.getCreatedBy(), details.getCreated());

        create.getReplyTo().tell(created);
        return effect.none();
    }

    @Override
    public State onCreatedDataset(CreatedDataset created) {
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onCreateDatasetAccessRequest(CreateDatasetAccessRequest create) {
        Optional<DatasetGrant> existing = this
            .details
            .getAcl()
            .findGrantByAuthorizationAndPrivilegeAndNotClosed(create.getGrantFor(), create.getGrant());

        if (existing.isPresent()) {
            CreatedDatasetAccessRequest created = CreatedDatasetAccessRequest.apply(create.getDataset(), existing.get());
            create.getReplyTo().tell(created);
            return effect.none();
        } else {
            Executed exec = Executed.apply(create.getExecutor().getUserId(), Instant.now());
            DatasetGrant grant;

            if (details.getAcl().canGrantDatasetAccess(create.getExecutor()) || !details.getGovernance().isApprovalRequired()) {
                grant = DatasetGrant.createApproved(
                    UID.apply(8),
                    create.getGrantFor(),
                    create.getGrant(),
                    create.getExecutor().getUserId(),
                    Instant.now(),
                    create.getJustification());
            } else {
                grant = DatasetGrant.createRequested(
                    UID.apply(8),
                    create.getGrantFor(),
                    create.getGrant(),
                    AccessRequest.apply(exec, create.getJustification()));
            }

            CreatedDatasetAccessRequest created = CreatedDatasetAccessRequest.apply(create.getDataset(), grant);

            return effect
                .persist(created)
                .thenRun(() -> create.getReplyTo().tell(created));
        }
    }

    @Override
    public State onCreatedDatasetAccessRequest(CreatedDatasetAccessRequest created) {
        DatasetACL acl = this.details.getAcl().withGrant(created.getGrant());
        this.details = this.details.withAcl(acl);
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
    public Effect<DatasetEvent, State> onGetAllVersions(GetAllVersions get) {
        actor.spawnAnonymous(CollectAllVersions.create(
            Lists.newArrayList(this.details.getVersions()),
            ImmutableMap.copyOf(versions),
            get));

        return effect.none();
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
        // TODO add list of versions?
        get.getReplyTo().tell(GetDetailsResult.apply(details));
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
        Optional<DatasetGrant> existing =
            details.getAcl().findGrantByAuthorizationAndPrivilegeAndNotClosed(grant.getGrantFor(), grant.getGrant());

        if (existing.isPresent()) {
            return existing
                .get()
                .approve(grant.getExecutor().getUserId(), Instant.now(), grant.getJustification().orElse(null))
                .map(
                    grant$updated -> {
                        GrantedDatasetAccess granted = GrantedDatasetAccess.apply(details.getDataset(), grant$updated);

                        return effect
                            .persist(granted)
                            .thenRun(() -> grant.getReplyTo().tell(granted));
                    },
                    errorMessage -> {
                        grant.getErrorTo().tell(errorMessage);
                        return effect.none();
                    });
        } else {
            DatasetGrant grant$new = DatasetGrant
                .createApproved(
                    UID.apply(8),
                    grant.getGrantFor(),
                    grant.getGrant(),
                    grant.getExecutor().getUserId(),
                    Instant.now(),
                    grant.getJustification().orElse(null));

            GrantedDatasetAccess granted = GrantedDatasetAccess.apply(details.getDataset(), grant$new);

            return effect
                .persist(granted)
                .thenRun(() -> grant.getReplyTo().tell(granted));
        }
    }

    @Override
    public State onGrantedDatasetAccess(GrantedDatasetAccess granted) {
        DatasetACL acl = details.getAcl().withGrant(granted.getGrant());
        details = details.withAcl(acl);
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onPublishCommittedDatasetVersion(PublishCommittedDatasetVersion publish) {
        VersionTag version = details
            .getVersions()
            .stream()
            .max(Comparator.comparing(VersionTagInfo::getVersion))
            .map(tag -> tag.nextVersion(publish.getSchema()))
            .orElse(VersionTag.apply(1, 0, 0));

        VersionTagInfo info = VersionTagInfo.apply(
            publish.getVersionId(),
            version,
            publish.getSchema(),
            publish.getRecords(),
            publish.getCommit());

        PublishedDatasetVersion published = PublishedDatasetVersion.apply(
            details.getDataset(),
            publish.getCommit(),
            info);

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
        this.details = details.withVersion(published.getVersion());
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
    public Effect<DatasetEvent, State> onRejectDatasetAccessRequest(RejectDatasetAccessRequest reject) {
        Optional<DatasetGrant> optGrant = details.getAcl().findGrantById(reject.getId());

        if (optGrant.isPresent()) {
            return optGrant
                .get()
                .reject(reject.getExecutor().getUserId(), Instant.now(), reject.getComment())
                .map(
                    grant$updated -> {
                        RejectedDatasetAccessRequest rejected = RejectedDatasetAccessRequest.apply(grant$updated);

                        return effect
                            .persist(rejected)
                            .thenRun(() -> reject.getReplyTo().tell(rejected));
                    },
                    errorMessage -> {
                        reject.getErrorTo().tell(errorMessage);
                        return effect.none();
                    });
        } else {
            reject.getErrorTo().tell(DatasetAccessRequestDoesNotExistError.apply(details.getDataset(), reject.getId()));
            return effect.none();
        }
    }

    @Override
    public State onRejectedDatasetAccessRequest(RejectedDatasetAccessRequest rejected) {
        DatasetACL acl = this.details.getAcl().withGrant(rejected.getGrant());
        this.details = this.details.withAcl(acl);
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onRevokeDatasetAccess(RevokeDatasetAccess revoke) {
        Optional<DatasetGrant> existing = details
            .getAcl()
            .findGrantByAuthorizationAndPrivilegeAndNotClosed(revoke.getRevokeFrom(), revoke.getRevoke())
            .map(Optional::of)
            .orElseGet(() -> details
                .getAcl()
                .findGrantByAuthorizationAndPrivilege(revoke.getRevokeFrom(), revoke.getRevoke()));

        if (existing.isPresent()) {
            DatasetGrant grant$updated = existing
                .get()
                .revoke(revoke.getExecutor().getUserId(), Instant.now(), revoke.getJustification().orElse(null));

            RevokedDatasetAccess revoked = RevokedDatasetAccess.apply(details.getDataset(), grant$updated);

            return effect
                .persist(revoked)
                .thenRun(() -> revoke.getReplyTo().tell(revoked));
        } else {
            DatasetGrant grant$revoked = DatasetGrant
                .createApproved(
                    UID.apply(8),
                    revoke.getRevokeFrom(),
                    revoke.getRevoke(),
                    revoke.getExecutor().getUserId(),
                    Instant.now(),
                    null)
                .revoke(revoke.getExecutor().getUserId(), Instant.now(), revoke.getJustification().orElse(null));

            RevokedDatasetAccess revoked = RevokedDatasetAccess.apply(details.getDataset(), grant$revoked);
            revoke.getReplyTo().tell(revoked);
            return effect.none();
        }
    }

    @Override
    public State onRevokedDatasetAccess(RevokedDatasetAccess revoked) {
        DatasetACL acl = details.getAcl().withGrant(revoked.getGrant());
        details = details.withAcl(acl);
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onRevokeDatasetAccessRequest(RevokeDatasetAccessRequest revoke) {
        Optional<DatasetGrant> existing = this.details.getAcl().findGrantById(revoke.getId());

        Executed executed = Executed.apply(revoke.getExecutor().getUserId(), Instant.now());
        RevokedDatasetAccessRequest revoked = RevokedDatasetAccessRequest.apply(
            executed,
            details.getDataset(),
            revoke.getComment().orElse(null),
            revoke.getId());

        if (existing.isPresent()) {
            return effect
                .persist(revoked)
                .thenRun(() -> revoke.getReplyTo().tell(revoked));
        } else {
            revoke.getReplyTo().tell(revoked);
            return effect.none();
        }
    }

    @Override
    public State onRevokedDatasetAccessRequest(RevokedDatasetAccessRequest revoked) {
        this
            .details
            .getAcl()
            .findGrantById(revoked.getId())
            .ifPresent(grant -> {
                DatasetGrant grant$updated = grant.revoke(
                    revoked.getExecuted().getBy(),
                    revoked.getExecuted().getAt(),
                    revoked.getJustification().orElse(null));

                DatasetACL acl = this.details.getAcl().withGrant(grant$updated);
                this.details = this.details.withAcl(acl);
            });

        return this;
    }

}
