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
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PushedData implements DatasetEvent, VersionEvent {

    private static final String DATASET = "dataset";
    private static final String DETAILS = "details";
    private static final String PUSHED_BY = "pushed-by";
    private static final String PUSHED_AT = "pushed-at";
    private static final String VERSION_ID = "version-id";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(VERSION_ID)
    private final UID versionId;

    @JsonProperty(PUSHED_BY)
    private final UserId pushedBy;

    @JsonProperty(PUSHED_AT)
    private final Instant pushedAt;

    @JsonProperty(DETAILS)
    private final VersionDetails details;

    @JsonCreator
    public static PushedData apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(VERSION_ID) UID versionId,
        @JsonProperty(PUSHED_BY) UserId pushedBy,
        @JsonProperty(PUSHED_AT) Instant pushedAt,
        @JsonProperty(DETAILS) VersionDetails details) {

        return new PushedData(dataset, versionId, pushedBy, pushedAt, details);
    }

}
