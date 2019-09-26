package maquette.controller.domain.entities.namespace.states;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;

import akka.actor.typed.javadsl.ActorContext;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectFactories;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.namespace.protocol.NamespaceEvent;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.commands.ChangeNamespaceDescription;
import maquette.controller.domain.entities.namespace.protocol.commands.ChangeNamespacePrivacy;
import maquette.controller.domain.entities.namespace.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.namespace.protocol.commands.CreateNamespace;
import maquette.controller.domain.entities.namespace.protocol.commands.DeleteNamespace;
import maquette.controller.domain.entities.namespace.protocol.commands.GrantNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.commands.RegisterDataset;
import maquette.controller.domain.entities.namespace.protocol.commands.RemoveDataset;
import maquette.controller.domain.entities.namespace.protocol.commands.RevokeNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.events.ChangedNamespaceDescription;
import maquette.controller.domain.entities.namespace.protocol.events.ChangedNamespacePrivacy;
import maquette.controller.domain.entities.namespace.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.namespace.protocol.events.CreatedNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.DeletedNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.GrantedNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.events.RegisteredDataset;
import maquette.controller.domain.entities.namespace.protocol.events.RemovedDataset;
import maquette.controller.domain.entities.namespace.protocol.events.RevokedNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceDetails;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceInfo;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceDetailsResult;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceInfoResult;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.namespace.NamespaceACL;
import maquette.controller.domain.values.namespace.NamespaceDetails;
import maquette.controller.domain.values.namespace.NamespaceGrant;
import maquette.controller.domain.values.namespace.NamespaceInfo;

@AllArgsConstructor(staticName = "apply")
public class ActiveNamespace implements State {

    private final ActorContext<NamespaceMessage> actor;

    private final EffectFactories<NamespaceEvent, State> effect;

    private NamespaceDetails details;

    @Override
    public Effect<NamespaceEvent, State> onChangeNamespaceDescription(ChangeNamespaceDescription change) {
        ChangedNamespaceDescription changed = ChangedNamespaceDescription.apply(
            change.getName(), change.getDescription(), change.getExecutor().getUserId(), Instant.now());

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
    public State onChangedNamespaceDescription(ChangedNamespaceDescription description) {
        this.details = this.details
            .withDescription(description.getDescription())
            .withModifiedBy(description.getChangedBy())
            .withModified(description.getChangedAt());

        return this;
    }

    @Override
    public Effect<NamespaceEvent, State> onChangeNamespacePrivacy(ChangeNamespacePrivacy change) {
        ChangedNamespacePrivacy changed = ChangedNamespacePrivacy.apply(
            details.getName(), change.isPrivate(), change.getExecutor().getUserId(), Instant.now());

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
    public State onChangedNamespacePrivacy(ChangedNamespacePrivacy changed) {
        NamespaceACL acl$new = details.getAcl().withPrivacy(changed.isPrivate());

        this.details = details
            .withAcl(acl$new)
            .withModified(changed.getChangedAt())
            .withModifiedBy(changed.getChangedBy());

        return this;
    }

    @Override
    public Effect<NamespaceEvent, State> onChangeOwner(ChangeOwner change) {
        if (change.getOwner().equals(details.getAcl().getOwner().getAuthorization())) {
            ChangedOwner changed = ChangedOwner.apply(details.getName(), details.getAcl().getOwner());
            change.getReplyTo().tell(changed);
            return effect.none();
        } else {
            GrantedAuthorization granted = GrantedAuthorization.apply(
                change.getExecutor().getUserId(),
                Instant.now(),
                change.getOwner());

            ChangedOwner changedOwner = ChangedOwner.apply(details.getName(), granted);

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
    public Effect<NamespaceEvent, State> onDeleteNamespace(DeleteNamespace deleteNamespace) {
        // TODO: Remove actual data?

        DeletedNamespace deleted = DeletedNamespace.apply(
            deleteNamespace.getName(),
            deleteNamespace.getExecutor().getUserId(),
            Instant.now());

        return effect
            .persist(deleted)
            .thenRun(() -> deleteNamespace.getReplyTo().tell(deleted));
    }

    @Override
    public State onDeletedNamespace(DeletedNamespace deletedNamespace) {
        return UninitializedNamespace.apply(actor, effect, deletedNamespace);
    }

    @Override
    public Effect<NamespaceEvent, State> onCreateNamespace(CreateNamespace create) {
        CreatedNamespace created = CreatedNamespace.apply(
            details.getName(), details.getAcl().isPrivate(), details.getCreatedBy(), details.getCreated());

        create.getReplyTo().tell(created);
        return effect.none();
    }

    @Override
    public State onCreatedNamespace(CreatedNamespace created) {
        return this;
    }

    @Override
    public Effect<NamespaceEvent, State> onGetNamespaceDetails(GetNamespaceDetails get) {
        get.getReplyTo().tell(GetNamespaceDetailsResult.apply(details));
        return effect.none();
    }

    @Override
    public Effect<NamespaceEvent, State> onGetNamespaceInfo(GetNamespaceInfo get) {
        NamespaceInfo info = NamespaceInfo.apply(
            details.getName(),
            details.getModified(),
            details.getAcl(),
            details.getDatasets());

        GetNamespaceInfoResult result = GetNamespaceInfoResult.apply(info);
        get.getReplyTo().tell(result);

        return effect.none();
    }

    @Override
    public Effect<NamespaceEvent, State> onGrantNamespaceAccess(GrantNamespaceAccess grant) {
        Optional<NamespaceGrant> existingGrant = details.getAcl().findGrant(grant.getGrantFor(), grant.getGrant());

        if (existingGrant.isPresent()) {
            GrantedNamespaceAccess granted = GrantedNamespaceAccess.apply(
                details.getName(),
                existingGrant.get().getPrivilege(),
                existingGrant.get().getAuthorization());

            grant.getReplyTo().tell(granted);

            return effect.none();
        } else {
            GrantedNamespaceAccess granted = GrantedNamespaceAccess.apply(
                details.getName(),
                grant.getGrant(),
                GrantedAuthorization.apply(grant.getExecutor().getUserId(), Instant.now(), grant.getGrantFor()));

            return effect
                .persist(granted)
                .thenRun(() -> grant.getReplyTo().tell(granted));
        }
    }

    @Override
    public State onGrantedNamespaceAccess(GrantedNamespaceAccess granted) {
        NamespaceACL acl = this
            .details
            .getAcl()
            .withGrant(granted.getGrantedFor(), granted.getGranted());

        this.details = this.details
            .withAcl(acl)
            .withModifiedBy(granted.getGrantedFor().getBy())
            .withModified(granted.getGrantedFor().getAt());

        return this;
    }

    @Override
    public Effect<NamespaceEvent, State> onRegisterDataset(RegisterDataset register) {
        RegisteredDataset registered = RegisteredDataset.apply(register.getDataset());

        if (details.getDatasets().contains(register.getDataset())) {
            register.getReplyTo().tell(registered);
            return effect.none();
        } else {
            return effect
                .persist(registered)
                .thenRun(() -> register.getReplyTo().tell(registered));
        }
    }

    @Override
    public State onRegisteredDataset(RegisteredDataset registered) {
        Set<ResourceName> datasets = Sets.newHashSet(this.details.getDatasets());
        datasets.add(registered.getDataset());

        this.details = details.withDatasets(datasets);
        return this;
    }

    @Override
    public Effect<NamespaceEvent, State> onRemoveDataset(RemoveDataset remove) {
        RemovedDataset removed = RemovedDataset.apply(remove.getDataset());

        if (this.details.getDatasets().contains(remove.getDataset())) {
            return effect
                .persist(removed)
                .thenRun(() -> remove.getReplyTo().tell(removed));
        } else {
            remove.getReplyTo().tell(removed);
            return effect.none();
        }
    }

    @Override
    public State onRemovedDataset(RemovedDataset removed) {
        Set<ResourceName> datasets = Sets.newHashSet(this.details.getDatasets());
        datasets.remove(removed.getDataset());

        this.details = details.withDatasets(datasets);
        return this;
    }

    @Override
    public Effect<NamespaceEvent, State> onRevokeNamespaceAccess(RevokeNamespaceAccess revoke) {
        Optional<NamespaceGrant> existingGrant = details.getAcl().findGrant(revoke.getRevokeFrom(), revoke.getRevoke());

        if (existingGrant.isPresent()) {
            RevokedNamespaceAccess revoked = RevokedNamespaceAccess.apply(
                details.getName(),
                existingGrant.get().getPrivilege(),
                existingGrant.get().getAuthorization());

            return effect
                .persist(revoked)
                .thenRun(() -> revoke.getReplyTo().tell(revoked));
        } else {
            GrantedAuthorization granted = GrantedAuthorization.apply(
                revoke.getExecutor().getUserId(),
                Instant.now(),
                revoke.getRevokeFrom());

            RevokedNamespaceAccess revoked = RevokedNamespaceAccess.apply(
                details.getName(),
                revoke.getRevoke(),
                granted);

            revoke.getReplyTo().tell(revoked);

            return effect.none();
        }
    }

    @Override
    public State onRevokedNamespaceAccess(RevokedNamespaceAccess revoked) {
        NamespaceACL acl = this
            .details
            .getAcl()
            .withoutGrant(revoked.getRevokedFrom().getAuthorization(), revoked.getRevoked());

        details = details
            .withAcl(acl)
            .withModifiedBy(revoked.getRevokedFrom().getBy())
            .withModified(revoked.getRevokedFrom().getAt());

        return this;
    }

}
