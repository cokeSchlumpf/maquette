package maquette.controller.domain.entities.project.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.ProjectEvent;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.project.ProjectPrivilege;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GrantedProjectAccess implements ProjectEvent {

    private static final String PROJECT = "project";
    private static final String GRANTED = "granted";
    private static final String GRANTED_FOR = "granted-for";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(GRANTED)
    private final ProjectPrivilege granted;

    @JsonProperty(GRANTED_FOR)
    private final GrantedAuthorization grantedFor;

    @JsonCreator
    public static GrantedProjectAccess apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(GRANTED) ProjectPrivilege granted,
        @JsonProperty(GRANTED_FOR) GrantedAuthorization grantedFor) {

        return new GrantedProjectAccess(project, granted, grantedFor);
    }

}
