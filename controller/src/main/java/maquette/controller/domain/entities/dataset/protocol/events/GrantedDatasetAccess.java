package maquette.controller.domain.entities.dataset.protocol.events;

import java.time.Instant;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GrantedDatasetAccess implements DatasetEvent {

    private static final String DATASET = "dataset";
    private static final String GRANTED = "granted";
    private static final String GRANTED_BY = "granted-by";
    private static final String GRANTED_AT = "granted-at";
    private static final String GRANTED_FOR = "granted-for";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(GRANTED)
    private final Set<DatasetPrivilege> granted;

    @JsonProperty(GRANTED_AT)
    private final Instant grantedAt;

    @JsonProperty(GRANTED_BY)
    private final UserId grantedBy;

    @JsonProperty(GRANTED_FOR)
    private final GrantedAuthorization grantedFor;

    @JsonCreator
    public static GrantedDatasetAccess apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(GRANTED) Set<DatasetPrivilege> granted,
        @JsonProperty(GRANTED_AT) Instant grantedAt,
        @JsonProperty(GRANTED_BY) UserId grantedBy,
        @JsonProperty(GRANTED_FOR) GrantedAuthorization grantedFor) {

        return new GrantedDatasetAccess(dataset, ImmutableSet.copyOf(granted), grantedAt, grantedBy, grantedFor);
    }

}
