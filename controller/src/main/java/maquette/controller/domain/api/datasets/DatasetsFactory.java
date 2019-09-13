package maquette.controller.domain.api.datasets;

import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.services.CreateDefaultNamespace;
import maquette.controller.domain.util.ActorPatterns;

@AllArgsConstructor(staticName = "apply")
public final class DatasetsFactory {

    private final ActorRef<ShardingEnvelope<NamespaceMessage>> namespaces;

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

    private final ActorPatterns patterns;

    private final CreateDefaultNamespace createDefaultNamespace;

    public Datasets create() {
        DatasetsImpl impl = DatasetsImpl.apply(namespaces, datasets, patterns);
        DatasetsSecured secured = DatasetsSecured.apply(namespaces, datasets, patterns, impl);
        return DatasetsUserActivity.apply(secured, createDefaultNamespace);
    }

}
