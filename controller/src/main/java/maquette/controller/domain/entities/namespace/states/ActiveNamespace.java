package maquette.controller.domain.entities.namespace.states;

import java.time.Instant;
import java.util.Optional;

import com.google.common.collect.Sets;

import akka.actor.typed.javadsl.ActorContext;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectFactories;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.namespace.protocol.NamespaceEvent;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.namespace.protocol.commands.CreateNamespace;
import maquette.controller.domain.entities.namespace.protocol.commands.DeleteNamespace;
import maquette.controller.domain.entities.namespace.protocol.commands.GrantNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.commands.RevokeNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.namespace.protocol.events.CreatedNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.DeletedNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.GrantedNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.events.RevokedNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceDetails;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceInfo;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceDetailsResult;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceInfoResult;
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
        this.details = this.details.withAcl(this.details.getAcl().withOwner(changed.getNewOwner()));
        return this;
    }

    @Override
    public Effect<NamespaceEvent, State> onDeleteNamespace(DeleteNamespace deleteNamespace) {
        // TODO: Is user authorized to delete?
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
        CreatedNamespace created = CreatedNamespace.apply(details.getName(), details.getCreatedBy(), details.getCreated());
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
        NamespaceInfo info = NamespaceInfo.apply(details.getName(), Sets.newHashSet());
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

        this.details = this.details.withAcl(acl);

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

        details = details.withAcl(acl);

        return this;
    }

}
