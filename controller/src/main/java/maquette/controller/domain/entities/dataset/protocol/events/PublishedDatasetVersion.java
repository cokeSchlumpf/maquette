package maquette.controller.domain.entities.dataset.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.Commit;
import maquette.controller.domain.values.dataset.VersionTagInfo;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PublishedDatasetVersion implements DatasetEvent {

    private static final String DATASET = "dataset";
    private static final String DETAILS = "details";
    private static final String VERSION = "version";

    @JsonProperty(DATASET)
    public final ResourcePath dataset;

    @JsonProperty(DETAILS)
    public final Commit details;

    @JsonProperty(VERSION)
    public final VersionTagInfo version;

    @JsonCreator
    public static PublishedDatasetVersion apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(DETAILS) Commit details,
        @JsonProperty(VERSION) VersionTagInfo version) {

        return new PublishedDatasetVersion(dataset, details, version);
    }

}
