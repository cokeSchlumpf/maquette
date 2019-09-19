package maquette.controller.domain.values.dataset;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VersionDoesNotExistError implements ErrorMessage {

    private final String message;

    public static VersionDoesNotExistError apply(ResourcePath dataset, UID version) {
        String message = String.format("Version '%s' does not exist in dataset '%s'.", version.getValue(), dataset.toString());
        return new VersionDoesNotExistError(message);
    }

}
