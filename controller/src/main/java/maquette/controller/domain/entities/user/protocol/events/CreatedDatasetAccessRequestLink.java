package maquette.controller.domain.entities.user.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.user.protocol.UserEvent;
import maquette.controller.domain.values.dataset.DatasetAccessRequestLink;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatedDatasetAccessRequestLink implements UserEvent {

    private static final String REQUEST = "request";

    @JsonProperty(REQUEST)
    private final DatasetAccessRequestLink request;

    @JsonCreator
    public static CreatedDatasetAccessRequestLink apply(
        @JsonProperty(REQUEST) DatasetAccessRequestLink request) {

        return new CreatedDatasetAccessRequestLink(request);
    }

}
