package maquette.controller.domain.entities.namespace.protocol.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.NamespaceEvent;
import maquette.controller.domain.entities.namespace.protocol.NamespacesEvent;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangedNamespaceDescription implements NamespaceEvent, NamespacesEvent {

    private static final String DESCRIPTION = "description";
    private static final String NAMESPACE = "namespace";
    private static final String CHANGED_AT = "changed-at";
    private static final String CHANGED_BY = "changed-by";

    @JsonProperty(NAMESPACE)
    private final ResourceName namespace;

    @JsonProperty(DESCRIPTION)
    private final Markdown description;

    @JsonProperty(CHANGED_BY)
    private final UserId changedBy;

    @JsonProperty(CHANGED_AT)
    private final Instant changedAt;

    @JsonCreator
    public static ChangedNamespaceDescription apply(
        @JsonProperty(NAMESPACE) ResourceName namespace,
        @JsonProperty(DESCRIPTION) Markdown description,
        @JsonProperty(CHANGED_BY) UserId changedBy,
        @JsonProperty(CHANGED_AT) Instant changedAt) {

        return new ChangedNamespaceDescription(namespace, description, changedBy, changedAt);
    }


}
