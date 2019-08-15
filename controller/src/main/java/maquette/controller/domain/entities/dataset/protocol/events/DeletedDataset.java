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
public class DeletedDataset implements DatasetEvent {

    private static final String DATASET = "dataset";
    private static final String DELETED_AT = "deleted-at";
    private static final String DELETED_BY = "deleted-by";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(DELETED_AT)
    private final Instant deletedAt;

    @JsonProperty(DELETED_BY)
    private final UserId deletedBy;

    @JsonCreator
    public static DeletedDataset apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(DELETED_AT) Instant deletedAt,
        @JsonProperty(DELETED_BY) UserId deletedBy) {

        return new DeletedDataset(dataset, deletedAt, deletedBy);
    }

}
