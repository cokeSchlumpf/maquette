package maquette.controller.domain.api.users;

import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.notifcation.protocol.NotificationsMessage;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.services.CreateDefaultProject;
import maquette.controller.domain.util.ActorPatterns;

@AllArgsConstructor(staticName = "apply")
public final class UsersFactory {

    private final ActorRef<ShardingEnvelope<UserMessage>> users;

    private final ActorRef<NotificationsMessage> notifications;

    private final ActorPatterns patterns;

    private final CreateDefaultProject createDefaultProject;

    public Users create() {
        UsersImpl impl = UsersImpl.apply(users, notifications, patterns);
        UsersSecured secured = UsersSecured.apply(impl, users, notifications, patterns);
        return UsersUserActivity.apply(secured, createDefaultProject);
    }

}
