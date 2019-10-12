package maquette.controller.domain.entities.dataset.protocol.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatedDataset implements DatasetEvent {

    private static final String CREATED_AT = "created-at";
    private static final String CREATED_BY = "created-by";
    private static final String IS_PRIVATE = "private";
    private static final String DATASET = "dataset";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(IS_PRIVATE)
    private final boolean isPrivate;

    @JsonProperty(CREATED_BY)
    private final UserId createdBy;

    @JsonProperty(CREATED_AT)
    private final Instant createdAt;

    @JsonCreator
    public static CreatedDataset apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(IS_PRIVATE) boolean isPrivate,
        @JsonProperty(CREATED_BY) UserId createdBy,
        @JsonProperty(CREATED_AT) Instant createdAt) {

        return new CreatedDataset(dataset, isPrivate, createdBy, createdAt);
    }

}
