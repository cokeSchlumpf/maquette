package maquette.controller.domain.entities.namespace.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.NamespaceEvent;
import maquette.controller.domain.values.core.ResourceName;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RemovedDataset implements NamespaceEvent {

    private static final String DATASET = "dataset";

    @JsonProperty(DATASET)
    private final ResourceName dataset;

    @JsonCreator
    public static RemovedDataset apply(
        @JsonProperty(DATASET) ResourceName dataset) {

        return new RemovedDataset(dataset);
    }

}
