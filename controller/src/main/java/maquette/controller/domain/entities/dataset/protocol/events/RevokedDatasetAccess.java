package maquette.controller.domain.entities.dataset.protocol.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetGrant;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RevokedDatasetAccess implements DatasetEvent {

    private static final String DATASET = "dataset";
    private static final String GRANT = "grant";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(GRANT)
    private final DatasetGrant grant;

    @JsonCreator
    public static RevokedDatasetAccess apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(GRANT) DatasetGrant grant) {

        return new RevokedDatasetAccess(dataset, grant);
    }

}
