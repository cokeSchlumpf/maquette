package maquette.controller.domain.entities.namespace.protocol.events;

import java.time.Instant;

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
public class RevokedNamespaceAccess {

    private static final String NAMESPACE = "namespace";
    private static final String REVOKED = "revoked";
    private static final String REVOKED_AT = "revoked-at";
    private static final String REVOKED_FROM = "revoked-from";

    @JsonProperty(NAMESPACE)
    private final ResourceName namespace;

    @JsonProperty(REVOKED)
    private final GrantedAuthorization revoked;

    @JsonProperty(REVOKED_FROM)
    private final UserId revokedFrom;

    @JsonProperty(REVOKED_AT)
    private final Instant revokedAt;

    @JsonCreator
    public static RevokedNamespaceAccess apply(
        @JsonProperty(NAMESPACE) ResourceName namespace,
        @JsonProperty(REVOKED) GrantedAuthorization revoked,
        @JsonProperty(REVOKED_FROM) UserId revokedFrom,
        @JsonProperty(REVOKED_AT) Instant revokedAt) {

        return new RevokedNamespaceAccess(namespace, revoked, revokedFrom, revokedAt);
    }

}
