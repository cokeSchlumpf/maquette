package maquette.controller.domain.values.dataset;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourcePath;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasetDoesNotExistError implements ErrorMessage {

    private final String message;

    public static DatasetDoesNotExistError apply(ResourcePath dataset) {
        String message = String.format("Dataset '%s' does not exist.", dataset.toString());
        return new DatasetDoesNotExistError(message);
    }

}
