package maquette.controller.domain.values.namespace;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.iam.GrantedAuthorization;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NamespaceGrant {

    private final GrantedAuthorization authorization;

    private final NamespacePrivilege privilege;

    @JsonCreator
    public static NamespaceGrant apply(
        @JsonProperty("authorization") GrantedAuthorization authorization,
        @JsonProperty("privilege") NamespacePrivilege privilege) {

        return new NamespaceGrant(authorization, privilege);
    }

}
