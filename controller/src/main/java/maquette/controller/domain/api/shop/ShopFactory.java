package maquette.controller.domain.api.shop;

import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.ProjectsMessage;
import maquette.controller.domain.services.CreateDefaultNamespace;
import maquette.controller.domain.util.ActorPatterns;

@AllArgsConstructor(staticName = "apply")
public final class ShopFactory {

    private final ActorRef<maquette.controller.domain.entities.project.protocol.ProjectsMessage> projectsRegistry;

    private final ActorRef<ProjectsMessage> namespacesRegistry;

    private final ActorRef<ShardingEnvelope<maquette.controller.domain.entities.project.protocol.ProjectMessage>> projects;

    private final ActorRef<ShardingEnvelope<ProjectMessage>> namespaces;

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

    private final ActorPatterns patterns;

    private final CreateDefaultNamespace createDefaultNamespace;

    public Shop create() {
        Shop impl = ShopImpl.apply(projectsRegistry, namespacesRegistry, projects, namespaces, datasets, patterns);
        return ShopUserActivity.apply(impl, createDefaultNamespace);
    }

}
