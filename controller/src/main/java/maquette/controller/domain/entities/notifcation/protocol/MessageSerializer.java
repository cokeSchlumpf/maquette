package maquette.controller.domain.entities.notifcation.protocol;

import java.util.Map;

import com.google.common.collect.Maps;

import akka.actor.ExtendedActorSystem;
import maquette.controller.domain.entities.notifcation.protocol.commands.CreateNotification;
import maquette.controller.domain.entities.notifcation.protocol.commands.MarkNotificationAsRead;
import maquette.controller.domain.entities.notifcation.protocol.events.CreatedNotification;
import maquette.controller.domain.entities.notifcation.protocol.events.MarkedNotificationAsRead;
import maquette.controller.domain.entities.notifcation.protocol.queries.GetNotification;
import maquette.controller.domain.entities.notifcation.protocol.queries.GetNotifications;
import maquette.controller.domain.entities.notifcation.protocol.results.GetNotificationResult;
import maquette.controller.domain.entities.notifcation.protocol.results.GetNotificationsResult;
import maquette.controller.domain.util.databind.AbstractMessageSerializer;

public final class MessageSerializer extends AbstractMessageSerializer {

    protected MessageSerializer(ExtendedActorSystem actorSystem) {
        super(actorSystem, 2403 + 4);
    }

    @Override
    protected Map<String, Class<?>> getManifestToClass() {
        Map<String, Class<?>> m = Maps.newHashMap();

        m.put("notifications/commands/create-notification/v1", CreateNotification.class);
        m.put("notifications/commands/mark-notification-as-read/v1", MarkNotificationAsRead.class);

        m.put("notifications/events/created-notification/v1", CreatedNotification.class);
        m.put("notifications/events/marked-notification-as-read/v1", MarkedNotificationAsRead.class);

        m.put("notifications/queries/get-notification/v1", GetNotification.class);
        m.put("notifications/queries/get-notifications/v1", GetNotifications.class);

        m.put("notifications/result/get-notification/v1", GetNotificationResult.class);
        m.put("notifications/result/get-notifications/v1", GetNotificationsResult.class);

        return m;
    }

}
