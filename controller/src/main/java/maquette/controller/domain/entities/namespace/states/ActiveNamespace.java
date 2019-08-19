package maquette.controller.domain.entities.namespace.states;

import com.google.common.collect.Sets;

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
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceInfoResult;
import maquette.controller.domain.values.namespace.NamespaceInfo;

@AllArgsConstructor(staticName = "apply")
public class ActiveNamespace implements State {

    private final ActorContext<NamespaceMessage> actor;

    private final EffectFactories<NamespaceEvent, State> effect;

    private final CreatedNamespace created;

    @Override
    public Effect<NamespaceEvent, State> onChangeOwner(ChangeOwner change) {
        return null;
    }

    @Override
    public State onChangedOwner(ChangedOwner changed) {
        return null;
    }

    @Override
    public Effect<NamespaceEvent, State> onCreateNamespace(CreateNamespace create) {
        create.getReplyTo().tell(created);
        return effect.none();
    }

    @Override
    public State onCreatedNamespace(CreatedNamespace created) {
        return this;
    }

    @Override
    public Effect<NamespaceEvent, State> onGetNamespaceInfo(GetNamespaceInfo get) {
        NamespaceInfo info = NamespaceInfo.apply(created.getNamespace(), Sets.newHashSet());
        GetNamespaceInfoResult result = GetNamespaceInfoResult.apply(info);
        get.getReplyTo().tell(result);

        return effect.none();
    }

}
