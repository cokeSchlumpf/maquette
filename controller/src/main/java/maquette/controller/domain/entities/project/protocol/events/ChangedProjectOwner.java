package maquette.controller.domain.entities.project.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.ProjectEvent;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.GrantedAuthorization;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangedProjectOwner implements ProjectEvent {

    private static final String PROJECT = "project";
    private static final String NEW_OWNER = "new-owner";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(NEW_OWNER)
    private final GrantedAuthorization newOwner;

    @JsonCreator
    public static ChangedProjectOwner apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(NEW_OWNER) GrantedAuthorization newOwner) {

        return new ChangedProjectOwner(project, newOwner);
    }

}
