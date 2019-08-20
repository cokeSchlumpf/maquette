package maquette.controller.domain.entities.dataset.protocol.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
public class RevokedDatasetAccess implements DatasetEvent {

    private static final String DATASET = "dataset";
    private static final String REVOKED = "revoked";
    private static final String REVOKED_BY = "revoked-by";
    private static final String REVOKED_AT = "revoked-at";
    private static final String REVOKED_FROM = "revoked-from";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(REVOKED)
    private final DatasetPrivilege revoked;

    @JsonProperty(REVOKED_AT)
    private final Instant grantedAt;

    @JsonProperty(REVOKED_BY)
    private final UserId revokedBy;

    @JsonProperty(REVOKED_FROM)
    private final GrantedAuthorization revokedFrom;

    @JsonCreator
    public static RevokedDatasetAccess apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(REVOKED) DatasetPrivilege revoked,
        @JsonProperty(REVOKED_AT) Instant revokedAt,
        @JsonProperty(REVOKED_BY) UserId revokedBy,
        @JsonProperty(REVOKED_FROM) GrantedAuthorization revokedFrom) {

        return new RevokedDatasetAccess(dataset, revoked, revokedAt, revokedBy, revokedFrom);
    }

}
