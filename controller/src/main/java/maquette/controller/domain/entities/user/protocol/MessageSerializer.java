package maquette.controller.domain.entities.user.protocol;

import java.util.Map;

import com.google.common.collect.Maps;

import akka.actor.ExtendedActorSystem;
import maquette.controller.domain.entities.user.protocol.commands.ConfigureNamespace;
import maquette.controller.domain.entities.user.protocol.commands.RegisterAccessToken;
import maquette.controller.domain.entities.user.protocol.commands.RemoveAccessToken;
import maquette.controller.domain.entities.user.protocol.commands.RenewAccessTokenSecret;
import maquette.controller.domain.entities.user.protocol.events.ConfiguredNamespace;
import maquette.controller.domain.entities.user.protocol.events.RegisteredAccessToken;
import maquette.controller.domain.entities.user.protocol.events.RemovedAccessToken;
import maquette.controller.domain.entities.user.protocol.queries.GetDetails;
import maquette.controller.domain.entities.user.protocol.results.GetDetailsResult;
import maquette.controller.domain.util.databind.AbstractMessageSerializer;

public class MessageSerializer extends AbstractMessageSerializer {

    protected MessageSerializer(ExtendedActorSystem actorSystem) {
        super(actorSystem, 2403 + 3);
    }

    @Override
    protected Map<String, Class<?>> getManifestToClass() {
        Map<String, Class<?>> m = Maps.newHashMap();

        m.put("users/commands/configure-namespace/v1", ConfigureNamespace.class);
        m.put("users/commands/register-access-token/v1", RegisterAccessToken.class);
        m.put("users/commands/remove-access-token/v1", RemoveAccessToken.class);
        m.put("users/commands/renew-access-token-secret/v1", RenewAccessTokenSecret.class);

        m.put("users/events/configured-namespace/v1", ConfiguredNamespace.class);
        m.put("users/events/registered-access-token/v1", RegisteredAccessToken.class);
        m.put("users/events/removed-access-token/v1", RemovedAccessToken.class);

        m.put("users/queries/get-details/v1", GetDetails.class);

        m.put("users/results/get-details/v1", GetDetailsResult.class);

        return m;
    }
}
