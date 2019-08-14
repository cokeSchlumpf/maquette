package maquette.controller.domain.entities.dataset.protocol.commands;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDataset;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.ErrorMessage;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(staticName = "apply")
public class CreateDataset implements DatasetMessage {

    private final ResourcePath path;

    private final User executor;

    private final ActorRef<CreatedDataset> replyTo;

    private final ActorRef<ErrorMessage> errorTo;

}
