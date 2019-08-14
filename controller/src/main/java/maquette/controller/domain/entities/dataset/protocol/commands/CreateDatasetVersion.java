package maquette.controller.domain.entities.dataset.protocol.commands;

import org.apache.avro.Schema;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.VersionMessage;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetVersion;
import maquette.controller.domain.values.iam.ErrorMessage;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(staticName = "apply")
public class CreateDatasetVersion implements DatasetMessage, VersionMessage {

    public final User executor;

    public final Schema schema;

    public final ActorRef<CreatedDatasetVersion> replyTo;

    public final ActorRef<ErrorMessage> errorTo;

}
