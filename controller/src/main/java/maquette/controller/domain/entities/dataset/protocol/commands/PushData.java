package maquette.controller.domain.entities.dataset.protocol.commands;

import java.util.List;

import org.apache.avro.generic.GenericData;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.VersionMessage;
import maquette.controller.domain.entities.dataset.protocol.events.PushedData;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.ErrorMessage;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(staticName = "apply")
public class PushData implements DatasetMessage, VersionMessage {

    private final UID id;

    private final User executor;

    private final List<GenericData.Record> records;

    private final ActorRef<PushedData> replyTo;

    private final ActorRef<ErrorMessage> errorTo;

}
