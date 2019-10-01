package maquette.controller.domain.values.dataset;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ErrorMessage;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserNamespaceAlreadyConfiguredError implements ErrorMessage {

    private final String message;

    public static UserNamespaceAlreadyConfiguredError apply() {
        return new UserNamespaceAlreadyConfiguredError("The user's namespace is already configured.");
    }

}
