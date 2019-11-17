package maquette.controller.domain.values.core.governance;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.UID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessRequestAlreadyRevokedError implements ErrorMessage {

    private final String message;

    public static AccessRequestAlreadyRevokedError apply(UID request) {
        String message = String.format("The access request '%s' has been already revoked.", request);
        return new AccessRequestAlreadyRevokedError(message);
    }

}
