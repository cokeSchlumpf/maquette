package maquette.controller.domain.entities.user.protocol;

import java.util.Map;

import com.google.common.collect.Maps;

import akka.actor.ExtendedActorSystem;
import maquette.controller.domain.entities.user.protocol.commands.RegisterAccessToken;
import maquette.controller.domain.entities.user.protocol.commands.RemoveAccessToken;
import maquette.controller.domain.entities.user.protocol.commands.RenewAccessTokenSecret;
import maquette.controller.domain.entities.user.protocol.events.RegisteredAccessToken;
import maquette.controller.domain.util.databind.AbstractMessageSerializer;

public class MessageSerializer extends AbstractMessageSerializer {

    protected MessageSerializer(ExtendedActorSystem actorSystem, int identifier) {
        super(actorSystem, 2403 + 3);
    }

    @Override
    protected Map<String, Class<?>> getManifestToClass() {
        Map<String, Class<?>> m = Maps.newHashMap();

        m.put("users/commands/register-access-token/v1", RegisterAccessToken.class);
        m.put("users/commands/remove-access-token/v1", RemoveAccessToken.class);
        m.put("users/commands/renew-access-token-secret/v1", RenewAccessTokenSecret.class);

        m.put("users/events/registered-access-token/v1", RegisteredAccessToken.class);

        return m;
    }
}
