package maquette.controller.domain.entities.project.protocol.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.Message;
import maquette.controller.domain.values.project.ProjectInfo;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetProjectInfoResult implements Message {

    private static final String INFO = "info";

    @JsonProperty(INFO)
    private final ProjectInfo info;

    @JsonCreator
    public static GetProjectInfoResult apply(
        @JsonProperty(INFO) ProjectInfo info) {

        return new GetProjectInfoResult(info);
    }

}
