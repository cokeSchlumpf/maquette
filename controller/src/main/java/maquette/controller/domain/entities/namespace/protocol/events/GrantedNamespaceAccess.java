package maquette.controller.domain.entities.namespace.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.NamespaceEvent;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.namespace.NamespacePrivilege;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GrantedNamespaceAccess implements NamespaceEvent {

    private static final String NAMESPACE = "namespace";
    private static final String GRANTED = "granted";
    private static final String GRANTED_FOR = "granted-for";

    @JsonProperty(NAMESPACE)
    private final ResourceName namespace;

    @JsonProperty(GRANTED)
    private final NamespacePrivilege granted;

    @JsonProperty(GRANTED_FOR)
    private final GrantedAuthorization grantedFor;

    @JsonCreator
    public static GrantedNamespaceAccess apply(
        @JsonProperty(NAMESPACE) ResourceName namespace,
        @JsonProperty(GRANTED) NamespacePrivilege granted,
        @JsonProperty(GRANTED_FOR) GrantedAuthorization grantedFor) {

        return new GrantedNamespaceAccess(namespace, granted, grantedFor);
    }

}
