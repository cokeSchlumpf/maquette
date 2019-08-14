package maquette.controller.domain.values.dataset;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class Version {

    private final int major;

    private final int minor;

    private final int patch;

}
