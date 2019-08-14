package maquette.controller.domain.entities.dataset.protocol.commands;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.events.GrantedDatasetAccess;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.ErrorMessage;
import maquette.controller.domain.values.iam.User;
import netscape.security.Privilege;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GrantDatasetAccess implements DatasetMessage {

    private final User executor;

    private final Set<Privilege> grant;

    private final Authorization grantFor;

    private final ActorRef<GrantedDatasetAccess> replyTo;

    private final ActorRef<ErrorMessage> errorTo;

    public static GrantDatasetAccess apply(
        User executor, Set<Privilege> grant, Authorization grantFor,
        ActorRef<GrantedDatasetAccess> replyTo, ActorRef<ErrorMessage> errorTo) {

        return new GrantDatasetAccess(executor, ImmutableSet.copyOf(grant), grantFor, replyTo, errorTo);
    }

}
