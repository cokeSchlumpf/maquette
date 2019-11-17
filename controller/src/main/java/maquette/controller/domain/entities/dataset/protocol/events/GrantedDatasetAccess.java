package maquette.controller.domain.entities.dataset.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetGrant;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GrantedDatasetAccess implements DatasetEvent {

    private static final String DATASET = "dataset";
    private static final String GRANT = "grant";
    private static final String GRANTED_FOR = "granted-for";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(GRANT)
    private final DatasetGrant grant;

    @JsonCreator
    public static GrantedDatasetAccess apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(GRANT) DatasetGrant granted) {

        return new GrantedDatasetAccess(dataset, granted);
    }

}
