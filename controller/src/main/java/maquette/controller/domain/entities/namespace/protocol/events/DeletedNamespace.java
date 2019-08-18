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
public class DeletedNamespace implements NamespaceEvent, NamespacesEvent {

    private static final String NAMESPACE = "namespace";
    private static final String DELETED_BY = "deleted-by";
    private static final String DELETED_AT = "deleted-at";

    @JsonProperty(NAMESPACE)
    private final ResourceName namespace;

    @JsonProperty(DELETED_BY)
    private final UserId deletedBy;

    @JsonProperty(DELETED_AT)
    private final Instant deletedAt;

    @JsonCreator
    public static DeletedNamespace apply(
        @JsonProperty(NAMESPACE) ResourceName namespace,
        @JsonProperty(DELETED_BY) UserId deletedBy,
        @JsonProperty(DELETED_AT) Instant deletedAt) {

        return new DeletedNamespace(namespace, deletedBy, deletedAt);
    }


}
