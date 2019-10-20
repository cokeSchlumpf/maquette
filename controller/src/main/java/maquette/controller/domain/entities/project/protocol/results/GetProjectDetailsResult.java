package maquette.controller.domain.entities.project.protocol.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.Message;
import maquette.controller.domain.values.project.ProjectDetails;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetProjectDetailsResult implements Message {

    private static final String DETAILS = "details";

    @JsonProperty(DETAILS)
    private final ProjectDetails details;

    @JsonCreator
    public static GetProjectDetailsResult apply(
        @JsonProperty(DETAILS) ProjectDetails details) {

        return new GetProjectDetailsResult(details);
    }

}
