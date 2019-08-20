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
public class RevokedNamespaceAccess implements NamespaceEvent {

    private static final String NAMESPACE = "namespace";
    private static final String REVOKED = "revoked";
    private static final String REVOKED_AT = "revoked-at";
    private static final String REVOKED_FROM = "revoked-from";

    @JsonProperty(NAMESPACE)
    private final ResourceName namespace;

    @JsonProperty(REVOKED)
    private final NamespacePrivilege revoked;

    @JsonProperty(REVOKED_FROM)
    private final GrantedAuthorization revokedFrom;

    @JsonCreator
    public static RevokedNamespaceAccess apply(
        @JsonProperty(NAMESPACE) ResourceName namespace,
        @JsonProperty(REVOKED) NamespacePrivilege revoked,
        @JsonProperty(REVOKED_FROM) GrantedAuthorization revokedFrom) {

        return new RevokedNamespaceAccess(namespace, revoked, revokedFrom);
    }

}
