package maquette.controller.domain.entities.namespace.protocol.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.NamespaceEvent;
import maquette.controller.domain.entities.namespace.protocol.NamespacesEvent;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatedNamespace implements NamespaceEvent, NamespacesEvent {

    private static final String NAMESPACE = "namespace";
    private static final String CREATED_BY = "created-by";
    private static final String CREATED_AT = "created-at";

    @JsonProperty(NAMESPACE)
    private final ResourceName namespace;

    @JsonProperty(CREATED_BY)
    private final UserId createdBy;

    @JsonProperty(CREATED_AT)
    private final Instant createdAt;

    @JsonCreator
    public static CreatedNamespace apply(
        @JsonProperty(NAMESPACE) ResourceName namespace,
        @JsonProperty(CREATED_BY) UserId createdBy,
        @JsonProperty(CREATED_AT) Instant createdAt) {

        return new CreatedNamespace(namespace, createdBy, createdAt);
    }

}
