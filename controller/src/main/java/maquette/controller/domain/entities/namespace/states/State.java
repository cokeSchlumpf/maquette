package maquette.controller.domain.entities.namespace.states;

import akka.persistence.typed.javadsl.Effect;
import maquette.controller.domain.entities.namespace.protocol.NamespaceEvent;
import maquette.controller.domain.entities.namespace.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.namespace.protocol.commands.CreateNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.namespace.protocol.events.CreatedNamespace;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceInfo;

public interface State {

    Effect<NamespaceEvent, State> onChangeOwner(ChangeOwner change);

    State onChangedOwner(ChangedOwner changed);

    Effect<NamespaceEvent, State> onCreateNamespace(CreateNamespace create);

    State onCreatedNamespace(CreatedNamespace created);

    Effect<NamespaceEvent, State> onGetNamespaceInfo(GetNamespaceInfo get);

}
