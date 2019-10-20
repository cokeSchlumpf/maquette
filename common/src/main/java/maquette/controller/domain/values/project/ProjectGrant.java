package maquette.controller.domain.values.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.iam.GrantedAuthorization;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectGrant {

    private final GrantedAuthorization authorization;

    private final NamespacePrivilege privilege;

    @JsonCreator
    public static ProjectGrant apply(
        @JsonProperty("authorization") GrantedAuthorization authorization,
        @JsonProperty("privilege") NamespacePrivilege privilege) {

        return new ProjectGrant(authorization, privilege);
    }

}
