package maquette.controller.domain.entities.namespace.states;

import akka.persistence.typed.javadsl.Effect;
import maquette.controller.domain.entities.namespace.protocol.NamespaceEvent;
import maquette.controller.domain.entities.namespace.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.namespace.protocol.commands.CreateNamespace;
import maquette.controller.domain.entities.namespace.protocol.commands.DeleteNamespace;
import maquette.controller.domain.entities.namespace.protocol.commands.GrantNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.commands.RegisterDataset;
import maquette.controller.domain.entities.namespace.protocol.commands.RemoveDataset;
import maquette.controller.domain.entities.namespace.protocol.commands.RevokeNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.namespace.protocol.events.CreatedNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.DeletedNamespace;
import maquette.controller.domain.entities.namespace.protocol.events.GrantedNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.events.RegisteredDataset;
import maquette.controller.domain.entities.namespace.protocol.events.RemovedDataset;
import maquette.controller.domain.entities.namespace.protocol.events.RevokedNamespaceAccess;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceDetails;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceInfo;

public interface State {

    Effect<NamespaceEvent, State> onChangeOwner(ChangeOwner change);

    State onChangedOwner(ChangedOwner changed);

    Effect<NamespaceEvent, State> onDeleteNamespace(DeleteNamespace deleteNamespace);

    State onDeletedNamespace(DeletedNamespace deletedNamespace);

    Effect<NamespaceEvent, State> onCreateNamespace(CreateNamespace create);

    State onCreatedNamespace(CreatedNamespace created);

    Effect<NamespaceEvent, State> onGetNamespaceDetails(GetNamespaceDetails get);

    Effect<NamespaceEvent, State> onGetNamespaceInfo(GetNamespaceInfo get);

    Effect<NamespaceEvent, State> onGrantNamespaceAccess(GrantNamespaceAccess grant);

    State onGrantedNamespaceAccess(GrantedNamespaceAccess granted);

    Effect<NamespaceEvent, State> onRegisterDataset(RegisterDataset register);

    State onRegisteredDataset(RegisteredDataset registered);

    Effect<NamespaceEvent, State> onRemoveDataset(RemoveDataset remove);

    State onRemovedDataset(RemovedDataset removed);

    Effect<NamespaceEvent, State> onRevokeNamespaceAccess(RevokeNamespaceAccess revoke);

    State onRevokedNamespaceAccess(RevokedNamespaceAccess revoked);

}
