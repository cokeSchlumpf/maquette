package maquette.controller.domain.values.namespace;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourceName;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectDoesNotExist implements ErrorMessage {

    private final String message;

    public static ProjectDoesNotExist apply(ResourceName name) {
        String message = String.format("Project '%s' does not exist.", name.getValue());
        return new ProjectDoesNotExist(message);
    }

}
