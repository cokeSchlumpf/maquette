package maquette.controller.domain.values.notification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationDoesNotExistError implements ErrorMessage {

    private final String message;

    public static NotificationDoesNotExistError apply(UID notificationId) {
        String message = String.format("Notification '%s' does not exist.", notificationId);
        return new NotificationDoesNotExistError(message);
    }

}
