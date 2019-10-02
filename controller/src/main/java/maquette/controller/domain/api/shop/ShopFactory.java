package maquette.controller.domain.api.shop;

import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.ProjectsMessage;
import maquette.controller.domain.services.CreateDefaultNamespace;
import maquette.controller.domain.util.ActorPatterns;

@AllArgsConstructor(staticName = "apply")
public final class ShopFactory {

    private final ActorRef<ProjectsMessage> projectsRegistry;

    private final ActorRef<NamespacesMessage> namespacesRegistry;

    private final ActorRef<ShardingEnvelope<ProjectMessage>> projects;

    private final ActorRef<ShardingEnvelope<NamespaceMessage>> namespaces;

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

    private final ActorPatterns patterns;

    private final CreateDefaultNamespace createDefaultNamespace;

    public Shop create() {
        Shop impl = ShopImpl.apply(projectsRegistry, namespacesRegistry, projects, namespaces, datasets, patterns);
        return ShopUserActivity.apply(impl, createDefaultNamespace);
    }

}
