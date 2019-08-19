package maquette.controller.domain.entities.namespace.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GrantedNamespaceAccess {

    private static final String NAMESPACE = "namespace";
    private static final String GRANTED = "granted";
    private static final String GRANTED_FOR = "granted-for";

    @JsonProperty(NAMESPACE)
    private final ResourceName namespace;

    @JsonProperty(GRANTED)
    private final GrantedAuthorization granted;

    @JsonProperty(GRANTED_FOR)
    private final UserId grantedFor;

    @JsonCreator
    public static GrantedNamespaceAccess apply(
        @JsonProperty(NAMESPACE) ResourceName namespace,
        @JsonProperty(GRANTED) GrantedAuthorization granted,
        @JsonProperty(GRANTED_FOR) UserId grantedFor) {

        return new GrantedNamespaceAccess(namespace, granted, grantedFor);
    }

}