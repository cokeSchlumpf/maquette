package maquette.controller.domain.entities.dataset.protocol.commands;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccess;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.ErrorMessage;
import maquette.controller.domain.values.iam.User;
import netscape.security.Privilege;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RevokeDatasetAccess implements DatasetMessage {

    private final User executor;

    private final Set<Privilege> revoke;

    private final Authorization revokeFrom;

    private final ActorRef<RevokedDatasetAccess> replyTo;

    private final ActorRef<ErrorMessage> errorTo;

    public static RevokeDatasetAccess apply(
        User executor, Set<Privilege> revoke, Authorization revokeFrom,
        ActorRef<RevokedDatasetAccess> replyTo, ActorRef<ErrorMessage> errorTo) {
        return new RevokeDatasetAccess(executor, ImmutableSet.copyOf(revoke), revokeFrom, replyTo, errorTo);
    }

}
