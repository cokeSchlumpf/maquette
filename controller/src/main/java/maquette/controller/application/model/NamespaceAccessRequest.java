package maquette.controller.application.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.project.ProjectPrivilege;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NamespaceAccessRequest {

    private final ProjectPrivilege privilege;

    private final Authorization authorization;

    @JsonCreator
    public static NamespaceAccessRequest apply(
        @JsonProperty("privilege") ProjectPrivilege privilege,
        @JsonProperty("authorization") Authorization authorization) {

        return new NamespaceAccessRequest(privilege, authorization);
    }

}
