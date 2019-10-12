package maquette.controller.domain.entities.project.protocol.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.ProjectEvent;
import maquette.controller.domain.entities.project.protocol.ProjectsEvent;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatedProject implements ProjectEvent, ProjectsEvent {

    private static final String PROJECT = "project";
    private static final String DESCRIPTION = "description";
    private static final String IS_PRIVATE = "private";
    private static final String CREATED_BY = "created-by";
    private static final String CREATED_AT = "created-at";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(DESCRIPTION)
    private final Markdown description;

    @JsonProperty(IS_PRIVATE)
    private final boolean isPrivate;

    @JsonProperty(CREATED_BY)
    private final UserId createdBy;

    @JsonProperty(CREATED_AT)
    private final Instant createdAt;

    @JsonCreator
    public static CreatedProject apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(DESCRIPTION) Markdown description,
        @JsonProperty(IS_PRIVATE) boolean isPrivate,
        @JsonProperty(CREATED_BY) UserId createdBy,
        @JsonProperty(CREATED_AT) Instant createdAt) {

        return new CreatedProject(project, description, isPrivate, createdBy, createdAt);
    }

}
