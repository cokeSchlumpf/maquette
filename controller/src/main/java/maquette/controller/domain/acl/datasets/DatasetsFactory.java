package maquette.controller.domain.acl.datasets;

import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.stream.Materializer;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.notifcation.protocol.NotificationsMessage;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.services.CreateDefaultProject;
import maquette.controller.domain.util.ActorPatterns;

@AllArgsConstructor(staticName = "apply")
public final class DatasetsFactory {

    private final ActorRef<ShardingEnvelope<ProjectMessage>> namespaces;

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

    private final ActorRef<ShardingEnvelope<UserMessage>> users;

    private final ActorRef<NotificationsMessage> notifications;

    private final ActorPatterns patterns;

    private final CreateDefaultProject createDefaultProject;

    private final Materializer materializer;

    public Datasets create() {
        DatasetsImpl impl = DatasetsImpl.apply(datasets, users, namespaces, notifications, patterns, materializer);
        DatasetsSecured secured = DatasetsSecured.apply(namespaces, datasets, patterns, impl);
        return DatasetsUserActivity.apply(secured, createDefaultProject);
    }

}
