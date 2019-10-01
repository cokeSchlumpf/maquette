package maquette.controller.domain.entities.namespace.states;

import java.time.Instant;

import com.google.common.collect.Sets;

import akka.actor.typed.javadsl.ActorContext;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectFactories;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.namespace.protocol.NamespaceEvent;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectDescription;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectPrivacy;
import maquette.controller.domain.entities.namespace.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.namespace.protocol.commands.CreateNamespace;
import maquette.controller.domain.entities.namespace.protocol.commands.DeleteNamespace;
import maquette.controller.domain.entities.namespace.protocol.commands.GrantNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.commands.RegisterDataset;
import maquette.controller.domain.entities.namespace.protocol.commands.RemoveDataset;
import maquette.controller.domain.entities.namespace.protocol.commands.RevokeNamespaceAccess;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectDescription;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectPrivacy;
import maquette.controller.domain.entities.namespace.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.namespace.protocol.events.CreatedNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.DeletedNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.GrantedNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.events.RegisteredDataset;
import maquette.controller.domain.entities.namespace.protocol.events.RemovedDataset;
import maquette.controller.domain.entities.namespace.protocol.events.RevokedNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceDetails;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceInfo;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.UserAuthorization;
import maquette.controller.domain.values.namespace.NamespaceACL;
import maquette.controller.domain.values.namespace.NamespaceDetails;
import maquette.controller.domain.values.namespace.NamespaceDoesNotExist;

@AllArgsConstructor(staticName = "apply")
public class UninitializedNamespace implements State {

    private final ActorContext<NamespaceMessage> actor;

    private final EffectFactories<NamespaceEvent, State> effect;

    private final DeletedNamespace deleted;

    public static UninitializedNamespace apply(
        ActorContext<NamespaceMessage> actor,
        EffectFactories<NamespaceEvent, State> effect) {

        return apply(actor, effect, null);
    }

    @Override
    public Effect<NamespaceEvent, State> onChangeOwner(ChangeOwner change) {
        change.getErrorTo().tell(NamespaceDoesNotExist.apply(change.getName()));
        return effect.none();
    }

    @Override
    public State onChangedOwner(ChangedOwner changed) {
        return this;
    }

    @Override
    public Effect<NamespaceEvent, State> onDeleteNamespace(DeleteNamespace deleteNamespace) {
        if (deleted != null) {
            deleteNamespace.getReplyTo().tell(deleted);
        } else {
            deleteNamespace.getReplyTo().tell(DeletedNamespace.apply(
                deleteNamespace.getName(),
                deleteNamespace.getExecutor().getUserId(),
                Instant.now()));
        }

        return effect.none();
    }

    @Override
    public State onDeletedNamespace(DeletedNamespace deletedNamespace) {
        return this;
    }

    @Override
    public Effect<NamespaceEvent, State> onCreateNamespace(CreateNamespace create) {
        CreatedNamespace created = CreatedNamespace.apply(
            create.getName(), create.isPrivate(), create.getExecutor().getUserId(), Instant.now());

        return effect
            .persist(created)
            .thenRun(() -> create.getReplyTo().tell(created));
    }

    @Override
    public State onCreatedNamespace(CreatedNamespace created) {
        final GrantedAuthorization owner = GrantedAuthorization.apply(
            created.getCreatedBy(),
            created.getCreatedAt(),
            UserAuthorization.apply(created.getCreatedBy()));

        NamespaceDetails details = NamespaceDetails.apply(
            created.getNamespace(),
            created.getCreatedBy(),
            created.getCreatedAt(),
            created.getCreatedBy(),
            created.getCreatedAt(),
            NamespaceACL.apply(owner, Sets.newHashSet(), created.isPrivate()),
            Sets.newHashSet());

        return ActiveNamespace.apply(actor, effect, details);
    }

    @Override
    public Effect<NamespaceEvent, State> onGetNamespaceDetails(GetNamespaceDetails get) {
        get.getErrorTo().tell(NamespaceDoesNotExist.apply(get.getNamespace()));
        return effect.none();
    }

    @Override
    public Effect<NamespaceEvent, State> onGetNamespaceInfo(GetNamespaceInfo get) {
        get.getErrorTo().tell(NamespaceDoesNotExist.apply(get.getNamespace()));
        return effect.none();
    }

    @Override
    public Effect<NamespaceEvent, State> onGrantNamespaceAccess(GrantNamespaceAccess grant) {
        grant.getErrorTo().tell(NamespaceDoesNotExist.apply(grant.getName()));
        return effect.none();
    }

    @Override
    public State onGrantedNamespaceAccess(GrantedNamespaceAccess granted) {
        return this;
    }

    @Override
    public Effect<NamespaceEvent, State> onRegisterDataset(RegisterDataset register) {
        register.getErrorTo().tell(NamespaceDoesNotExist.apply(register.getNamespace()));
        return effect.none();
    }

    @Override
    public State onRegisteredDataset(RegisteredDataset registered) {
        return this;
    }

    @Override
    public Effect<NamespaceEvent, State> onRemoveDataset(RemoveDataset remove) {
        remove.getErrorTo().tell(NamespaceDoesNotExist.apply(remove.getName()));
        return effect.none();
    }

    @Override
    public State onRemovedDataset(RemovedDataset removed) {
        return this;
    }

    @Override
    public Effect<NamespaceEvent, State> onRevokeNamespaceAccess(RevokeNamespaceAccess revoke) {
        revoke.getErrorTo().tell(NamespaceDoesNotExist.apply(revoke.getName()));
        return effect.none();
    }

    @Override
    public State onRevokedNamespaceAccess(RevokedNamespaceAccess revoked) {
        return this;
    }

}
