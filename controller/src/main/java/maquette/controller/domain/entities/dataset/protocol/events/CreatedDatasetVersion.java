package maquette.controller.domain.entities.dataset.protocol.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.entities.dataset.protocol.VersionEvent;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatedDatasetVersion implements DatasetEvent, VersionEvent {

    private static final String CREATED_AT = "created-at";
    private static final String CREATED_BY = "created-by";
    private static final String VERSION_ID = "version-id";
    private static final String DATASET = "dataset";

    @JsonProperty(DATASET)
    public final ResourcePath dataset;

    @JsonProperty(VERSION_ID)
    public final UID versionId;

    @JsonProperty(CREATED_BY)
    public final UserId createdBy;

    @JsonProperty(CREATED_AT)
    public final Instant createdAt;

    @JsonCreator
    public static CreatedDatasetVersion apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(VERSION_ID) UID versionId,
        @JsonProperty(CREATED_BY) UserId createdBy,
        @JsonProperty(CREATED_AT) Instant createdAt) {

        return new CreatedDatasetVersion(dataset, versionId, createdBy, createdAt);
    }


}
