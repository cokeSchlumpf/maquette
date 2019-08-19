package maquette.controller.domain.entities.namespace.states;

import java.time.Instant;

import akka.actor.typed.javadsl.ActorContext;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectFactories;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.namespace.protocol.NamespaceEvent;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.namespace.protocol.commands.CreateNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.namespace.protocol.events.CreatedNamespace;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceInfo;

@AllArgsConstructor(staticName = "apply")
public class UninitializedNamespace implements State {

    private final ActorContext<NamespaceMessage> actor;

    private final EffectFactories<NamespaceEvent, State> effect;

    @Override
    public Effect<NamespaceEvent, State> onChangeOwner(ChangeOwner change) {
        return effect.none();
    }

    @Override
    public State onChangedOwner(ChangedOwner changed) {
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
        return ActiveNamespace.apply(actor, effect, created);
    }

    @Override
    public Effect<NamespaceEvent, State> onGetNamespaceInfo(GetNamespaceInfo get) {
        return effect.none();
    }

}
