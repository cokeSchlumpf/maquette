package maquette.controller.domain.entities.project.protocol.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.ProjectEvent;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangedProjectDescription implements ProjectEvent {

    private static final String DESCRIPTION = "description";
    private static final String PROJECT = "project";
    private static final String CHANGED_AT = "changed-at";
    private static final String CHANGED_BY = "changed-by";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(DESCRIPTION)
    private final Markdown description;

    @JsonProperty(CHANGED_BY)
    private final UserId changedBy;

    @JsonProperty(CHANGED_AT)
    private final Instant changedAt;

    @JsonCreator
    public static ChangedProjectDescription apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(DESCRIPTION) Markdown description,
        @JsonProperty(CHANGED_BY) UserId changedBy,
        @JsonProperty(CHANGED_AT) Instant changedAt) {

        return new ChangedProjectDescription(project, description, changedBy, changedAt);
    }


}
