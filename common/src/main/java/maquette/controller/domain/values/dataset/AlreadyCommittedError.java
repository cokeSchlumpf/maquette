package maquette.controller.domain.values.dataset;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ErrorMessage;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AlreadyCommittedError implements ErrorMessage {

    private final String message;

    public static AlreadyCommittedError apply() {
        return new AlreadyCommittedError("The version is already committed. No data can be attached anymore.");
    }

}
