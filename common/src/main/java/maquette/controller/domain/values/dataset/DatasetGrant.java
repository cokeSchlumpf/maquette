package maquette.controller.domain.values.dataset;

import java.time.Instant;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.Executed;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.Ok;
import maquette.controller.domain.values.core.Result;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.governance.AccessRequest;
import maquette.controller.domain.values.core.governance.AccessRequestAlreadyDecidedError;
import maquette.controller.domain.values.core.governance.AccessRequestAlreadyRevokedError;
import maquette.controller.domain.values.core.governance.AccessRequestResponse;
import maquette.controller.domain.values.core.governance.AccessRevoke;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasetGrant {

    private static final String ID = "id";
    private static final String GRANT_FOR = "grant-for";
    private static final String GRANT = "grant";
    private static final String REQUEST = "request";
    private static final String REQUEST_RESPONSE = "request-response";
    private static final String REVOKE = "revoke";

    @JsonProperty(ID)
    private final UID id;

    @JsonProperty(GRANT_FOR)
    private final Authorization grantFor;

    @JsonProperty(GRANT)
    private final DatasetPrivilege grant;

    @JsonProperty(REQUEST)
    private final AccessRequest request;

    @JsonProperty(REQUEST_RESPONSE)
    private final AccessRequestResponse requestResponse;

    @JsonProperty(REVOKE)
    private final AccessRevoke revoke;

    @JsonCreator
    public static DatasetGrant apply(
        @JsonProperty(ID) UID id,
        @JsonProperty(GRANT_FOR) Authorization grantFor,
        @JsonProperty(GRANT) DatasetPrivilege grant,
        @JsonProperty(REQUEST) AccessRequest request,
        @JsonProperty(REQUEST_RESPONSE) AccessRequestResponse requestResponse,
        @JsonProperty(REVOKE) AccessRevoke revoke) {

        return new DatasetGrant(id, grantFor, grant, request, requestResponse, revoke);
    }

    public static DatasetGrant createRequested(
        UID id, Authorization grantFor, DatasetPrivilege grant, AccessRequest request) {

        return apply(id, grantFor, grant, request, null, null);
    }

    public static DatasetGrant createApproved(
        UID id, Authorization grantFor, DatasetPrivilege grant, UserId executor, Instant executedAt, Markdown justification) {

        Executed executed = Executed.apply(executor, executedAt);
        return apply(id, grantFor, grant, null, AccessRequestResponse.approved(executed, justification), null);
    }

    public Optional<DatasetMember> asDatasetMember() {
        if (isActive()) {
            GrantedAuthorization granted = GrantedAuthorization.apply(
                requestResponse.getExecuted().getBy(),
                requestResponse.getExecuted().getAt(),
                grantFor);

            return Optional.of(DatasetMember.apply(granted, grant));
        } else {
            return Optional.empty();
        }
    }

    public Result<DatasetGrant> approve(UserId executor, Instant executedAt, Markdown justification) {
        return approve$validate()
            .map(
                ok -> {
                    if (getRequestResponse().isPresent()) {
                        return Result.success(this);
                    } else {
                        Executed executed = Executed.apply(executor, executedAt);
                        AccessRequestResponse approved = AccessRequestResponse.approved(executed, justification);

                        return Result.success(apply(id, grantFor, grant, request, approved, null));
                    }
                },
                Result::failure);
    }

    private Result<Ok> approve$validate() {
        if (getRequestResponse().isPresent() && !getRequestResponse().get().isApproved()) {
            return Result.failure(AccessRequestAlreadyDecidedError.apply(id));
        } else if (getRevoke().isPresent()) {
            return Result.failure(AccessRequestAlreadyRevokedError.apply(id));
        } else {
            return Result.success(Ok.INSTANCE);
        }
    }

    public boolean isSimilar(DatasetGrant grant) {
        // TODO mw: Implement
        return false;
    }

    public Result<DatasetGrant> reject(UserId executor, Instant executedAt, Markdown justification) {
        return reject$validate()
            .map(
                ok -> {
                    if (getRequestResponse().isPresent()) {
                        return Result.success(this);
                    } else {
                        Executed executed = Executed.apply(executor, executedAt);
                        AccessRequestResponse rejected = AccessRequestResponse.rejected(executed, justification);

                        return Result.success(apply(id, grantFor, grant, request, rejected, null));
                    }
                },
                Result::failure);
    }

    private Result<Ok> reject$validate() {
        if (getRequestResponse().isPresent() && getRequestResponse().get().isApproved()) {
            return Result.failure(AccessRequestAlreadyDecidedError.apply(id));
        } else if (getRevoke().isPresent()) {
            return Result.failure(AccessRequestAlreadyRevokedError.apply(id));
        } else {
            return Result.success(Ok.INSTANCE);
        }
    }

    public DatasetGrant revoke(UserId executor, Instant executedAt, Markdown justification) {
        if (getRevoke().isPresent()) {
            return this;
        } else {
            Executed executed = Executed.apply(executor, executedAt);
            AccessRevoke revoke = AccessRevoke.apply(executed, justification);
            return apply(id, grantFor, grant, request, requestResponse, revoke);
        }
    }

    public boolean isActive() {
        return !getRevoke().isPresent() && getRequestResponse().map(AccessRequestResponse::isApproved).orElse(false);
    }

    public boolean isClosed() {
        return getRevoke().isPresent() || getRequestResponse().map(AccessRequestResponse::isApproved).map(b -> !b).orElse(false);
    }

    public boolean isOpen() {
        return !(isActive() || isClosed());
    }

    public Optional<AccessRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    public Optional<AccessRequestResponse> getRequestResponse() {
        return Optional.ofNullable(requestResponse);
    }

    public Optional<AccessRevoke> getRevoke() {
        return Optional.ofNullable(revoke);
    }

}
