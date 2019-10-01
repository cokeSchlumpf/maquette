package maquette.controller.domain.entities.project.protocol.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.NamespaceEvent;
import maquette.controller.domain.entities.namespace.protocol.NamespacesEvent;
import maquette.controller.domain.entities.project.protocol.ProjectEvent;
import maquette.controller.domain.entities.project.protocol.ProjectsEvent;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.UserId;
import maquette.controller.domain.values.project.ProjectProperties;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatedProject implements ProjectEvent, ProjectsEvent {

    private static final String PROJECT = "project";
    private static final String PROPERTIES = "properties";
    private static final String CREATED_BY = "created-by";
    private static final String CREATED_AT = "created-at";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(PROPERTIES)
    private final ProjectProperties properties;

    @JsonProperty(CREATED_BY)
    private final UserId createdBy;

    @JsonProperty(CREATED_AT)
    private final Instant createdAt;

    @JsonCreator
    public static CreatedProject apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(PROPERTIES) ProjectProperties properties,
        @JsonProperty(CREATED_BY) UserId createdBy,
        @JsonProperty(CREATED_AT) Instant createdAt) {

        return new CreatedProject(project, properties, createdBy, createdAt);
    }

}
