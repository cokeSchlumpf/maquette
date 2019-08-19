package maquette.controller.domain.entities.namespace.protocol.queries;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.entities.namespace.protocol.results.ListNamespacesResult;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ListNamespaces implements NamespacesMessage {

    public static final String REPLY_TO = "reply-to";

    @JsonProperty(REPLY_TO)
    private final ActorRef<ListNamespacesResult> replyTo;

    @JsonCreator
    public static ListNamespaces apply(
        @JsonProperty(REPLY_TO) ActorRef<ListNamespacesResult> replyTo) {
        return new ListNamespaces(replyTo);
    }

}
