package maquette.controller.domain.values.dataset;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.Test;

import maquette.controller.domain.values.core.Executed;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.governance.AccessRequest;
import maquette.controller.domain.values.core.governance.AccessRequestResponse;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.UserAuthorization;
import maquette.controller.domain.values.iam.UserId;

public class DatasetGrantUTest {

    /**
     * When a grant is created directly (without Dataset Access Request), the request is
     * created with {@link DatasetGrant#createApproved(UID, Authorization, DatasetPrivilege, UserId, Instant, Markdown)}.
     *
     * In that case the Grant immediately contains the Approved response.
     */
    @Test
    public void createApproved() {
        /*
         * Given
         */
        var uid = UID.apply();
        var user = UserAuthorization.random();
        var granted = Instant.now();
        var privilege = DatasetPrivilege.CONSUMER;
        var executor = UserId.random();

        var grantedAuth = GrantedAuthorization.apply(executor, granted, user);
        var member = DatasetMember.apply(grantedAuth, privilege);

        /*
         * When
         */
        var grant = DatasetGrant.createApproved(
            uid, user, privilege,
            executor, granted, Markdown.lorem());

        /*
         * Then
         */
        assertThat(grant.getRequest())
            .as("As it was immediately generated as approved, there should not be a request")
            .isNotPresent();

        assertThat(grant.getRequestResponse())
            .as("The request response should be set and it should be approved")
            .isPresent()
            .satisfies(ro -> ro.ifPresent(r -> {
                assertThat(r)
                    .extracting(o -> o.getExecuted().getBy(), o -> o.getExecuted().getAt())
                    .containsExactly(executor, granted);
            }));

        assertThat(grant)
            .as("Since the request is approved, the grant should be active")
            .satisfies(g -> {
                assertThat(g.asDatasetMember())
                    .isPresent()
                    .get()
                    .isEqualTo(member);

                assertThat(g.isOpen()).isFalse();
                assertThat(g.isActive()).isTrue();
                assertThat(g.isClosed()).isFalse();
            });
    }

    /**
     * When an access request is created by a user. The request contains the access request detail. But the
     * grant is not active and cannot be used.
     */
    @Test
    public void createRequested() {
        /*
         * Given
         */
        var uid = UID.apply();
        var grantFor = UserAuthorization.random();
        var privilege = DatasetPrivilege.CONSUMER;

        var executed = Executed.now(UserId.random());
        var request = AccessRequest.apply(executed, Markdown.lorem());

        /*
         * When
         */
        var grant = DatasetGrant.createRequested(uid, grantFor, privilege, request);

        /*
         * Then
         */
        assertThat(grant.getRequestResponse())
            .as("There shouldn't be a response for the grant.")
            .isNotPresent();

        assertThat(grant.getRequest())
            .as("The request should be available in the grant.")
            .isPresent()
            .satisfies(ro -> ro.ifPresent(r -> {
                assertThat(r).isEqualTo(request);
            }));

        assertThat(grant)
            .as("Since the request is not approved, the grant is not active")
            .satisfies(g -> {
                assertThat(g.asDatasetMember()).isNotPresent();
                assertThat(g.isOpen()).isTrue();
                assertThat(g.isActive()).isFalse();
                assertThat(g.isClosed()).isFalse();
            });
    }

    @Test
    public void approveGrant() {
        /*
         * Given
         */
        var uid = UID.apply();
        var grantFor = UserAuthorization.random();
        var privilege = DatasetPrivilege.CONSUMER;

        var executed = Executed.now(UserId.random());
        var request = AccessRequest.apply(executed, Markdown.lorem());
        var grant = DatasetGrant.createRequested(uid, grantFor, privilege, request);

        var approvedBy = UserId.random();
        var approvedAt = Instant.now();
        var justification = Markdown.lorem();

        var grantedAuth = GrantedAuthorization.apply(approvedBy, approvedAt, grantFor);
        var member = DatasetMember.apply(grantedAuth, privilege);

        /*
         * When
         */
        grant = grant
            .approve(approvedBy, approvedAt, justification)
            .map(g -> g, e -> null);

        /*
         * Then
         */
        assertThat(grant.getRequest())
            .as("The request should be available in the grant.")
            .isPresent()
            .satisfies(ro -> ro.ifPresent(r -> {
                assertThat(r).isEqualTo(request);
            }));

        assertThat(grant.getRequestResponse())
            .as("The request response should be set and it should be approved")
            .isPresent()
            .satisfies(ro -> ro.ifPresent(r -> {
                assertThat(r)
                    .extracting(
                        o -> o.getExecuted().getBy(),
                        o -> o.getExecuted().getAt(),
                        AccessRequestResponse::isApproved)
                    .containsExactly(approvedBy, approvedAt, true);
            }));

        assertThat(grant)
            .as("Since the request is approved, the grant should be active")
            .satisfies(g -> {
                assertThat(g.asDatasetMember())
                    .isPresent()
                    .get()
                    .isEqualTo(member);

                assertThat(g.isOpen()).isFalse();
                assertThat(g.isActive()).isTrue();
                assertThat(g.isClosed()).isFalse();
            });
    }

    @Test
    public void rejectGrant() {
        /*
         * Given
         */
        var uid = UID.apply();
        var grantFor = UserAuthorization.random();
        var privilege = DatasetPrivilege.CONSUMER;

        var executed = Executed.now(UserId.random());
        var request = AccessRequest.apply(executed, Markdown.lorem());
        var grant = DatasetGrant.createRequested(uid, grantFor, privilege, request);

        var rejectBy = UserId.random();
        var rejectAt = Instant.now();
        var justification = Markdown.lorem();

        var grantedAuth = GrantedAuthorization.apply(rejectBy, rejectAt, grantFor);
        var member = DatasetMember.apply(grantedAuth, privilege);

        /*
         * When
         */
        grant = grant
            .reject(rejectBy, rejectAt, justification)
            .map(g -> g, e -> null);

        /*
         * Then
         */
        assertThat(grant.getRequest())
            .as("The request should be available in the grant.")
            .isPresent()
            .satisfies(ro -> ro.ifPresent(r -> {
                assertThat(r).isEqualTo(request);
            }));

        assertThat(grant.getRequestResponse())
            .as("The request response should be set and it should be rejected")
            .isPresent()
            .satisfies(ro -> ro.ifPresent(r -> {
                assertThat(r)
                    .extracting(
                        o -> o.getExecuted().getBy(),
                        o -> o.getExecuted().getAt(),
                        AccessRequestResponse::isApproved)
                    .containsExactly(rejectBy, rejectAt, false);
            }));

        assertThat(grant)
            .as("Since the request is approved, the grant should be active")
            .satisfies(g -> {
                assertThat(g.asDatasetMember()).isNotPresent();
                assertThat(g.isOpen()).isFalse();
                assertThat(g.isActive()).isFalse();
                assertThat(g.isClosed()).isTrue();
            });
    }

}