package maquette.controller.domain.api.users;

import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.util.ActorPatterns;

@AllArgsConstructor(staticName = "apply")
public final class UsersFactory {

    private final ActorRef<ShardingEnvelope<UserMessage>> users;
    private final ActorPatterns patterns;

    public Users create() {
        UsersImpl impl = UsersImpl.apply(users, patterns);
        return UsersSecured.apply(impl, users, patterns);
    }

}
