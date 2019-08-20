package maquette.controller.domain.entities.namespace.protocol.queries;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceDetailsResult;
import maquette.controller.domain.values.core.ResourceName;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetNamespaceDetails implements NamespaceMessage {

    public static final String REPLY_TO = "reply-to";
    public static final String NAMESPACE = "namespace";

    @JsonProperty(NAMESPACE)
    private final ResourceName namespace;

    @JsonProperty(REPLY_TO)
    private final ActorRef<GetNamespaceDetailsResult> replyTo;

    @JsonCreator
    public static GetNamespaceDetails apply(
        @JsonProperty(NAMESPACE) ResourceName namespace,
        @JsonProperty(REPLY_TO) ActorRef<GetNamespaceDetailsResult> replyTo) {

        return new GetNamespaceDetails(namespace, replyTo);
    }

}
