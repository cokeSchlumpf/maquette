package maquette.controller.domain.api.datasets;

import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.stream.Materializer;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.services.CreateDefaultNamespace;
import maquette.controller.domain.util.ActorPatterns;

@AllArgsConstructor(staticName = "apply")
public final class DatasetsFactory {

    private final ActorRef<NamespacesMessage> namespaceRegistry;

    private final ActorRef<ShardingEnvelope<NamespaceMessage>> namespaces;

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

    private final ActorRef<ShardingEnvelope<UserMessage>> users;

    private final ActorPatterns patterns;

    private final CreateDefaultNamespace createDefaultNamespace;

    private final Materializer materializer;

    public Datasets create() {
        DatasetsImpl impl = DatasetsImpl.apply(namespaceRegistry, namespaces, datasets, users, patterns, materializer);
        DatasetsSecured secured = DatasetsSecured.apply(namespaces, datasets, patterns, impl);
        return DatasetsUserActivity.apply(secured, createDefaultNamespace);
    }

}
