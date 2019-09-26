package maquette.controller.domain.entities.namespace.protocol;

import java.util.Map;

import com.google.common.collect.Maps;

import akka.actor.ExtendedActorSystem;
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
import maquette.controller.domain.entities.namespace.protocol.queries.ListNamespaces;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceDetailsResult;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceInfoResult;
import maquette.controller.domain.entities.namespace.protocol.results.ListNamespacesResult;
import maquette.controller.domain.util.databind.AbstractMessageSerializer;

public final class MessageSerializer extends AbstractMessageSerializer {

    protected MessageSerializer(ExtendedActorSystem actorSystem) {
        super(actorSystem, 2403 + 1);
    }

    @Override
    protected Map<String, Class<?>> getManifestToClass() {
        Map<String, Class<?>> m = Maps.newHashMap();

        m.put("namespace/commands/namespace-description/v1", ChangeNamespaceDescription.class);
        m.put("namespace/commands/change-namespace-privacy/v1", ChangeNamespacePrivacy.class);
        m.put("namespace/commands/change-owner/v1", ChangeOwner.class);
        m.put("namespace/commands/create-namespace/v1", CreateNamespace.class);
        m.put("namespace/commands/delete-namespace/v1", DeleteNamespace.class);
        m.put("namespace/commands/grant-namespace-access/v1", GrantNamespaceAccess.class);
        m.put("namespace/commands/register-dataset/v1", RegisterDataset.class);
        m.put("namespaces/commands/remove-dataset/v1", RemoveDataset.class);
        m.put("namespace/commands/revoke-namespace-access/v1", RevokeNamespaceAccess.class);

        m.put("namespace/events/changed-namespace-description/v1", ChangedNamespaceDescription.class);
        m.put("namespace/events/changed-namespace-privacy/v1", ChangedNamespacePrivacy.class);
        m.put("namespace/events/changed-owner/v1", ChangedOwner.class);
        m.put("namespace/events/created-namespace/v1", CreatedNamespace.class);
        m.put("namespace/events/deleted-namespace/v1", DeletedNamespace.class);
        m.put("namespace/events/granted-namespace-access/v1", GrantedNamespaceAccess.class);
        m.put("namespace/events/registered-dataset/v1", RegisteredDataset.class);
        m.put("namespace/events/removed-dataset/v1", RemovedDataset.class);
        m.put("namespace/events/revoked-namespace-access/v1", RevokedNamespaceAccess.class);

        m.put("namespace/queries/get-namespace-details/v1", GetNamespaceDetails.class);
        m.put("namespace/queries/get-namespace-info/v1", GetNamespaceInfo.class);
        m.put("namespace/queries/list-namespaces/v1", ListNamespaces.class);

        m.put("namespaces/results/get-namespace-details/v1", GetNamespaceDetailsResult.class);
        m.put("namespaces/results/get-namespaces-info/v1", GetNamespaceInfoResult.class);
        m.put("namespaces/results/list-namespaces/v1", ListNamespacesResult.class);

        return m;
    }

}
