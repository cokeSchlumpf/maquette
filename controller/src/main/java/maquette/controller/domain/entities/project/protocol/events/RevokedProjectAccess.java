package maquette.controller.domain.entities.project.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.ProjectEvent;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.project.NamespacePrivilege;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RevokedProjectAccess implements ProjectEvent {

    private static final String PROJECT = "project";
    private static final String REVOKED = "revoked";
    private static final String REVOKED_AT = "revoked-at";
    private static final String REVOKED_FROM = "revoked-from";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(REVOKED)
    private final NamespacePrivilege revoked;

    @JsonProperty(REVOKED_FROM)
    private final GrantedAuthorization revokedFrom;

    @JsonCreator
    public static RevokedProjectAccess apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(REVOKED) NamespacePrivilege revoked,
        @JsonProperty(REVOKED_FROM) GrantedAuthorization revokedFrom) {

        return new RevokedProjectAccess(project, revoked, revokedFrom);
    }

}
