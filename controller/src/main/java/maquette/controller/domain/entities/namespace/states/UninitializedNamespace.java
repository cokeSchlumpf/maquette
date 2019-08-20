package maquette.controller.domain.entities.namespace.states;

import java.time.Instant;

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
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.UserAuthorization;
import maquette.controller.domain.values.namespace.NamespaceACL;
import maquette.controller.domain.values.namespace.NamespaceDetails;

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
        CreatedNamespace created = CreatedNamespace.apply(create.getName(), create.getExecutor().getUserId(), Instant.now());

        return effect
            .persist(created)
            .thenRun(() -> create.getReplyTo().tell(created));
    }

    @Override
    public State onCreatedNamespace(CreatedNamespace created) {
        final GrantedAuthorization owner = GrantedAuthorization.apply(
            created.getCreatedBy(),
            created.getCreatedAt(),
            UserAuthorization.apply(created.getCreatedBy().getName()));

        NamespaceDetails details = NamespaceDetails.apply(
            created.getNamespace(),
            created.getCreatedBy(),
            created.getCreatedAt(),
            created.getCreatedBy(),
            created.getCreatedAt(),
            NamespaceACL.apply(owner, Sets.newHashSet()));

        return ActiveNamespace.apply(actor, effect, details);
    }

    @Override
    public Effect<NamespaceEvent, State> onGetNamespaceDetails(GetNamespaceDetails get) {
        return effect.none();
    }

    @Override
    public Effect<NamespaceEvent, State> onGetNamespaceInfo(GetNamespaceInfo get) {
        return effect.none();
    }

    @Override
    public Effect<NamespaceEvent, State> onGrantNamespaceAccess(GrantNamespaceAccess grant) {
        return effect.none();
    }

    @Override
    public State onGrantedNamespaceAccess(GrantedNamespaceAccess granted) {
        return this;
    }

    @Override
    public Effect<NamespaceEvent, State> onRevokeNamespaceAccess(RevokeNamespaceAccess revoke) {
        return effect.none();
    }

    @Override
    public State onRevokedNamespaceAccess(RevokedNamespaceAccess revoked) {
        return this;
    }

}
