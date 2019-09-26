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
public class ChangedNamespacePrivacy implements NamespaceEvent, NamespacesEvent {

    private static final String IS_PRIVATE = "is-private";
    private static final String NAMESPACE = "namespace";
    private static final String CHANGED_AT = "changed-at";
    private static final String CHANGED_BY = "changed-by";

    @JsonProperty(NAMESPACE)
    private final ResourceName namespace;

    @JsonProperty(IS_PRIVATE)
    private final boolean isPrivate;

    @JsonProperty(CHANGED_BY)
    private final UserId changedBy;

    @JsonProperty(CHANGED_AT)
    private final Instant changedAt;

    @JsonCreator
    public static ChangedNamespacePrivacy apply(
        @JsonProperty(NAMESPACE) ResourceName namespace,
        @JsonProperty(IS_PRIVATE) boolean isPrivate,
        @JsonProperty(CHANGED_BY) UserId changedBy,
        @JsonProperty(CHANGED_AT) Instant changedAt) {

        return new ChangedNamespacePrivacy(namespace, isPrivate, changedBy, changedAt);
    }

    @Deprecated
    public static ChangedNamespacePrivacy apply(ResourceName namespace, UserId createdBy, Instant createdAt) {
        return new ChangedNamespacePrivacy(namespace, false, createdBy, createdAt);
    }


}
