package maquette.controller.domain.entities.dataset.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetAccessRequest;
import maquette.controller.domain.values.dataset.DatasetGrant;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatedDatasetAccessRequest implements DatasetEvent {

    private static final String DATASET = "dataset";
    private static final String GRANT = "grant";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(GRANT)
    private final DatasetGrant grant;

    @JsonCreator
    public static CreatedDatasetAccessRequest apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(GRANT) DatasetGrant grant) {

        return new CreatedDatasetAccessRequest(dataset, grant);
    }

}
