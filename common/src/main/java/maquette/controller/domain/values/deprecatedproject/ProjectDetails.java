package maquette.controller.domain.values.deprecatedproject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectDetails {

    private static final String PROPERTIES = "properties";
    private static final String DETAILS = "details";

    @JsonProperty(PROPERTIES)
    private final ProjectProperties properties;

    @JsonProperty(DETAILS)
    private final maquette.controller.domain.values.project.ProjectDetails details;

    @JsonCreator
    public static ProjectDetails apply(
        @JsonProperty(PROPERTIES) ProjectProperties properties,
        @JsonProperty(DETAILS) maquette.controller.domain.values.project.ProjectDetails details) {

        return new ProjectDetails(properties, details);
    }

}
