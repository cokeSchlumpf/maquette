package maquette.controller.domain.entities.dataset.protocol.events;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.values.core.Executed;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RevokedDatasetAccessRequest implements DatasetEvent {

    private static final String DATASET = "dataset";
    private static final String EXECUTED = "executed";
    private static final String JUSTIFICATION = "justification";
    private static final String GRANT = "grant";

    @JsonProperty(EXECUTED)
    private final Executed executed;

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(JUSTIFICATION)
    private final Markdown justification;

    @JsonProperty(GRANT)
    private final UID id;

    @JsonCreator
    public static RevokedDatasetAccessRequest apply(
        @JsonProperty(EXECUTED) Executed executed,
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(JUSTIFICATION) Markdown justification,
        @JsonProperty(GRANT) UID id) {

        return new RevokedDatasetAccessRequest(executed, dataset, justification, id);
    }

    public static RevokedDatasetAccessRequest apply(Executed executed, ResourcePath dataset, UID grant) {

        return apply(executed, dataset, null, grant);
    }

    public Optional<Markdown> getJustification() {
        return Optional.ofNullable(justification);
    }

}
