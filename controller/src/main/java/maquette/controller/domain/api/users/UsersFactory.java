package maquette.controller.domain.api.users;

import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.services.CreateDefaultNamespace;
import maquette.controller.domain.util.ActorPatterns;

@AllArgsConstructor(staticName = "apply")
public final class UsersFactory {

    private final ActorRef<ShardingEnvelope<NamespaceMessage>> namespaces;

    private final ActorRef<ShardingEnvelope<UserMessage>> users;

    private final ActorPatterns patterns;

    private final CreateDefaultNamespace createDefaultNamespace;

    public Users create() {
        UsersImpl impl = UsersImpl.apply(users, patterns);
        UsersSecured secured = UsersSecured.apply(impl, users, patterns);
        return UsersUserActivity.apply(secured, createDefaultNamespace);
    }

}
