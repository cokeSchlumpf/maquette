package maquette.controller.domain.entities.dataset.protocol.events;

import java.time.Instant;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatedDataset implements DatasetEvent {

    private static final String CREATED = "created";
    private static final String CREATED_BY = "created-by";
    private static final String DATASET = "dataset";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(CREATED_BY)
    private final UserId createdBy;

    @JsonProperty(CREATED)
    private final Instant created;

    @JsonCreator
    public static CreatedDataset apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(CREATED_BY) UserId createdBy,
        @JsonProperty(CREATED) Instant created) {

        return new CreatedDataset(dataset, createdBy, created);
    }

}
