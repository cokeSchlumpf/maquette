package maquette.controller.domain.values.dataset;

import java.time.Instant;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.governance.Approved;
import maquette.controller.domain.values.core.governance.Rejected;
import maquette.controller.domain.values.core.governance.Revoked;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.UserId;

@Value
@Wither
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasetAccessRequest {

    private static final String ID = "id";
    private static final String INITIATED_BY = "initiated-by";
    private static final String INITIATED = "initiated";
    private static final String JUSTIFICATION = "justification";
    private static final String GRANT = "grant";
    private static final String GRANT_FOR = "grant-for";
    private static final String APPROVED = "approved";
    private static final String REJECTED = "rejected";
    private static final String REVOKED = "revoked";

    @JsonProperty(ID)
    private final UID id;

    @JsonProperty(INITIATED_BY)
    private final UserId initiatedBy;

    @JsonProperty(INITIATED)
    private final Instant initiated;

    @JsonProperty(JUSTIFICATION)
    private final String justification;

    @JsonProperty(GRANT)
    private final DatasetPrivilege grant;

    @JsonProperty(GRANT_FOR)
    private final Authorization grantFor;

    @JsonProperty(APPROVED)
    private final Approved approved;

    @JsonProperty(REJECTED)
    private final Rejected rejected;

    @JsonProperty(REVOKED)
    private final Revoked revoked;

    @JsonCreator
    public static DatasetAccessRequest apply(
        @JsonProperty(ID) UID id,
        @JsonProperty(INITIATED_BY) UserId initiatedBy,
        @JsonProperty(INITIATED) Instant initiated,
        @JsonProperty(JUSTIFICATION) String justification,
        @JsonProperty(GRANT) DatasetPrivilege grant,
        @JsonProperty(GRANT_FOR) Authorization grantFor,
        @JsonProperty(APPROVED) Approved approved,
        @JsonProperty(REJECTED) Rejected rejected,
        @JsonProperty(REVOKED) Revoked revoked) {

        return new DatasetAccessRequest(id, initiatedBy, initiated, justification, grant, grantFor, approved, rejected, revoked);
    }

    public static DatasetAccessRequest apply(UID id, UserId initiatedBy, Instant initiated, String justification, DatasetPrivilege grant,
                                             Authorization grantFor) {
        return apply(id, initiatedBy, initiated, justification, grant, grantFor, null, null, null);
    }

    public Optional<Approved> getApproved() {
        return Optional.ofNullable(approved);
    }

    public Optional<Rejected> getRejected() { return Optional.ofNullable(rejected); }

    public Optional<Revoked> getRevoked() {
        return Optional.ofNullable(revoked);
    }

}
