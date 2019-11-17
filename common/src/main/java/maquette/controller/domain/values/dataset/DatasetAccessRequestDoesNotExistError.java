package maquette.controller.domain.values.dataset;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasetAccessRequestDoesNotExistError implements ErrorMessage {

    private final String message;

    public static DatasetAccessRequestDoesNotExistError apply(ResourcePath dataset, UID request) {
        String message = String.format("The access request '%s' does not exist in dataset '%s'.", request, dataset);
        return new DatasetAccessRequestDoesNotExistError(message);
    }

}
