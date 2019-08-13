package maquette.controller.domain.entities.dataset.protocol;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.iam.ErrorMessage;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(staticName = "apply")
public class CreateDataset {

    private final ResourcePath path;

    private final User executor;

    private final ActorRef<CreatedDataset> replyTo;

    private final ActorRef<ErrorMessage> errorTo;

}
