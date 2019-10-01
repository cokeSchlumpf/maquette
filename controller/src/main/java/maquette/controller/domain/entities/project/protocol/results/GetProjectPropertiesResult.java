package maquette.controller.domain.entities.project.protocol.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.Message;
import maquette.controller.domain.values.project.ProjectProperties;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetProjectPropertiesResult implements Message {

    private static final String PROPERTIES = "properties";

    @JsonProperty(PROPERTIES)
    private final ProjectProperties properties;

    @JsonCreator
    public static GetProjectPropertiesResult apply(
        @JsonProperty(PROPERTIES)ProjectProperties properties) {

        return new GetProjectPropertiesResult(properties);
    }

}
