package maquette.controller.application.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.namespace.NamespacePrivilege;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NamespaceAccessRequest {

    private final NamespacePrivilege privilege;

    private final Authorization authorization;

    @JsonCreator
    public static NamespaceAccessRequest apply(
        @JsonProperty("privilege") NamespacePrivilege privilege,
        @JsonProperty("authorization") Authorization authorization) {

        return new NamespaceAccessRequest(privilege, authorization);
    }

}
