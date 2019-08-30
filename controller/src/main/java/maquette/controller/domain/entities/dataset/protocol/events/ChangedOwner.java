package maquette.controller.domain.entities.dataset.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.GrantedAuthorization;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangedOwner implements DatasetEvent {

    private static final String DATASET = "dataset";
    private static final String NEW_OWNER = "new-owner";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(NEW_OWNER)
    private final GrantedAuthorization newOwner;

    @JsonCreator
    public static ChangedOwner apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(NEW_OWNER) GrantedAuthorization newOwner) {

        return new ChangedOwner(dataset, newOwner);
    }

}
