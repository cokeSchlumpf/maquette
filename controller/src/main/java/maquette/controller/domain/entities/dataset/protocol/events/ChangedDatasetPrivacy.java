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
public class ChangedDatasetPrivacy implements DatasetEvent {

    private static final String CHANGED_AT = "changed-at";
    private static final String CHANGED_BY = "changed-by";
    private static final String IS_PRIVATE = "is-private";
    private static final String DATASET = "dataset";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(IS_PRIVATE)
    private final boolean isPrivate;

    @JsonProperty(CHANGED_BY)
    private final UserId changedBy;

    @JsonProperty(CHANGED_AT)
    private final Instant changedAt;

    @JsonCreator
    public static ChangedDatasetPrivacy apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(IS_PRIVATE) boolean isPrivate,
        @JsonProperty(CHANGED_BY) UserId changedBy,
        @JsonProperty(CHANGED_AT) Instant changedAt) {

        return new ChangedDatasetPrivacy(dataset, isPrivate, changedBy, changedAt);
    }

}
