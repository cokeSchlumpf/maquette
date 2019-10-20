package maquette.controller.domain.values.project;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourceName;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NamespaceDoesNotExist implements ErrorMessage {

    private final String message;

    public static NamespaceDoesNotExist apply(ResourceName name) {
        String message = String.format("Namespace '%s' does not exist.", name.getValue());
        return new NamespaceDoesNotExist(message);
    }

}
