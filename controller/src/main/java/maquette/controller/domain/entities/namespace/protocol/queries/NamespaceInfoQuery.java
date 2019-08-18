package maquette.controller.domain.entities.namespace.protocol.queries;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.actor.typed.ActorRef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespaceInfo;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NamespaceInfoQuery implements NamespaceMessage {

    public static final String REPLY_TO = "reply-to";
    public static final String NAMESPACE = "namespace";
    public static final String EXECUTOR = "executor";

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(NAMESPACE)
    private final ResourceName namespace;

    @JsonProperty(REPLY_TO)
    private final ActorRef<NamespaceInfo> replyTo;

    @JsonCreator
    public static NamespaceInfoQuery apply(
        @JsonProperty(EXECUTOR) User executor,
        @JsonProperty(NAMESPACE) ResourceName namespace,
        @JsonProperty(REPLY_TO) ActorRef<NamespaceInfo> replyTo) {

        return new NamespaceInfoQuery(executor, namespace, replyTo);
    }

}
