package maquette.controller.domain.entities.deprecatedproject.protocol.results;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.deprecatedproject.protocol.Message;
import maquette.controller.domain.values.core.ResourceName;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ListProjectsResult implements Message {

    private static final String PROJECTS = "projects";

    @JsonProperty(PROJECTS)
    private final Set<ResourceName> projects;

    @JsonCreator
    public static ListProjectsResult apply(
        @JsonProperty(PROJECTS) Set<ResourceName> projects) {

        return new ListProjectsResult(ImmutableSet.copyOf(projects));
    }

}
