package maquette.controller.domain.values.dataset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.time.Instant;
import java.util.Set;

import org.junit.Test;

import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.governance.AccessRequest;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.UserAuthorization;
import maquette.controller.domaina.values.iam.UserId;

public class DatasetACLUTest {

    /**
     * A consumer should receive rights to consume data, find the dataset and read the dataset details.
     */
    @Test
    public void grantConsumer() {
        /*
         * Given
         */
        var owner = GrantedAuthorization.apply(UserId.random(), Instant.now(), UserAuthorization.random());
        var acl = DatasetACL.apply(owner, Set.of(), false);

        var grantForUserId = UserId.random();
        var grantForUser = AuthenticatedUser.apply(grantForUserId);
        var grantFor = UserAuthorization.apply(grantForUserId);
        var grant = DatasetGrant.createApproved(
            UID.apply(), grantFor, DatasetPrivilege.CONSUMER,
            UserId.random(), Instant.now(), Markdown.lorem());

        /*
         * When
         */
        acl = acl.withGrant(grant);

        /*
         * Then
         */
        assertThat(acl.canConsume(grantForUser)).isTrue();
        assertThat(acl.canFind(grantForUser)).isTrue();
        assertThat(acl.canReadDetails(grantForUser)).isTrue();

        assertThat(acl.canGrantDatasetAccess(grantForUser)).isFalse();
        assertThat(acl.canManage(grantForUser)).isFalse();
        assertThat(acl.canProduce(grantForUser)).isFalse();
        assertThat(acl.canRevokeDatasetAccess(grantForUser)).isFalse();
    }

    /**
     * A consumer should receive rights to produce data, find the dataset and read the dataset details.
     */
    @Test
    public void grantProduce() {
        /*
         * Given
         */
        var owner = GrantedAuthorization.apply(UserId.random(), Instant.now(), UserAuthorization.random());
        var acl = DatasetACL.apply(owner, Set.of(), false);

        var grantForUserId = UserId.random();
        var grantForUser = AuthenticatedUser.apply(grantForUserId);
        var grantFor = UserAuthorization.apply(grantForUserId);
        var grant = DatasetGrant.createApproved(
            UID.apply(), grantFor, DatasetPrivilege.PRODUCER,
            UserId.random(), Instant.now(), Markdown.lorem());

        /*
         * When
         */
        acl = acl.withGrant(grant);

        /*
         * Then
         */
        assertThat(acl.canProduce(grantForUser)).isTrue();
        assertThat(acl.canFind(grantForUser)).isTrue();
        assertThat(acl.canReadDetails(grantForUser)).isTrue();

        assertThat(acl.canGrantDatasetAccess(grantForUser)).isFalse();
        assertThat(acl.canManage(grantForUser)).isFalse();
        assertThat(acl.canConsume(grantForUser)).isFalse();
        assertThat(acl.canRevokeDatasetAccess(grantForUser)).isFalse();
    }

    /**
     * An admin should receive all rights for the dataset
     */
    @Test
    public void grantAdmin() {
        /*
         * Given
         */
        var owner = GrantedAuthorization.apply(UserId.random(), Instant.now(), UserAuthorization.random());
        var acl = DatasetACL.apply(owner, Set.of(), false);

        var grantForUserId = UserId.random();
        var grantForUser = AuthenticatedUser.apply(grantForUserId);
        var grantFor = UserAuthorization.apply(grantForUserId);
        var grant = DatasetGrant.createApproved(
            UID.apply(), grantFor, DatasetPrivilege.ADMIN,
            UserId.random(), Instant.now(), Markdown.lorem());

        /*
         * When
         */
        acl = acl.withGrant(grant);

        /*
         * Then
         */
        assertThat(acl.canProduce(grantForUser)).isTrue();
        assertThat(acl.canFind(grantForUser)).isTrue();
        assertThat(acl.canReadDetails(grantForUser)).isTrue();

        assertThat(acl.canGrantDatasetAccess(grantForUser)).isTrue();
        assertThat(acl.canManage(grantForUser)).isTrue();
        assertThat(acl.canConsume(grantForUser)).isTrue();
        assertThat(acl.canRevokeDatasetAccess(grantForUser)).isTrue();
    }

    /**
     * Only active grants will count as members of the dataset.
     */
    @Test
    public void members() {
        /*
         * Given
         */
        var owner = GrantedAuthorization.apply(UserId.random(), Instant.now(), UserAuthorization.random());

        var adminUserId = UserId.random();
        var consumerUserId = UserId.random();
        var producerUserId = UserId.random();
        var requestedUserId = UserId.random();
        var rejectedUserId = UserId.random();

        var adminAuth = UserAuthorization.apply(adminUserId);
        var consumerAuth = UserAuthorization.apply(consumerUserId);
        var producerAuth = UserAuthorization.apply(producerUserId);
        var requestedAuth = UserAuthorization.apply(requestedUserId);
        var rejectedAuth = UserAuthorization.apply(rejectedUserId);

        var adminGrant = DatasetGrant.createApproved(
            UID.apply(), adminAuth, DatasetPrivilege.ADMIN,
            UserId.random(), Instant.now(), Markdown.lorem());
        var consumerGrant = DatasetGrant.createApproved(
            UID.apply(), consumerAuth, DatasetPrivilege.CONSUMER,
            UserId.random(), Instant.now(), Markdown.lorem());
        var producerGrant = DatasetGrant.createApproved(
            UID.apply(), producerAuth, DatasetPrivilege.PRODUCER,
            UserId.random(), Instant.now(), Markdown.lorem());
        var requestedGrant = DatasetGrant.createRequested(
            UID.apply(), requestedAuth, DatasetPrivilege.CONSUMER, AccessRequest.fake());
        var rejectedGrant = DatasetGrant
            .createRequested(UID.apply(), rejectedAuth, DatasetPrivilege.PRODUCER, AccessRequest.fake())
            .reject(UserId.random(), Instant.now(), Markdown.lorem())
            .orElseThrow();

        /*
         * When
         */
        var acl = DatasetACL
            .apply(owner, Set.of(), false)
            .withGrant(adminGrant)
            .withGrant(consumerGrant)
            .withGrant(producerGrant)
            .withGrant(requestedGrant)
            .withGrant(rejectedGrant);

        /*
         * Then
         */
        assertThat(acl.getMembers())
            .as("only active grants should be in member list of the dataset")
            .hasSize(3)
            .extracting(m -> m.getAuthorization().getAuthorization(), DatasetMember::getPrivilege)
            .contains(tuple(adminAuth, DatasetPrivilege.ADMIN))
            .contains(tuple(consumerAuth, DatasetPrivilege.CONSUMER))
            .contains(tuple(producerAuth, DatasetPrivilege.PRODUCER));
    }

}