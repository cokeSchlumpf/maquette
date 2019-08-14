package maquette.controller.domain.entities.dataset.protocol.events;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(staticName = "apply")
public class PushedData {

    private final ResourcePath path;

    private final UID id;

    private final long pushedCount;

    private final UserId pushedBy;

    private final Instant pushedAt;

    private final VersionDetails properties;

}
