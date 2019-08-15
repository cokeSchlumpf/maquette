package maquette.controller.domain.entities.dataset.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.dataset.PublishDetails;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PublishedDatasetVersion {

    private static final String DATASET = "dataset";
    private static final String DETAILS = "details";
    private static final String VERSION_ID = "version-id";

    @JsonProperty(DATASET)
    public final ResourcePath dataset;

    @JsonProperty(VERSION_ID)
    public final UID versionId;

    @JsonProperty(DETAILS)
    public final PublishDetails details;

    @JsonCreator
    public static PublishedDatasetVersion apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(VERSION_ID) UID id,
        @JsonProperty(DETAILS) PublishDetails details) {

        return new PublishedDatasetVersion(dataset, id, details);
    }

}
