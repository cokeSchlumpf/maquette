package maquette.controller.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.ExecutionException;

import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetGrant;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.RoleAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserAuthorization;
import maquette.controller.domain.values.namespace.NamespaceDetails;
import maquette.controller.domain.values.namespace.NamespacePrivilege;
import maquette.util.TestSetup;

public class DatasetsITest {

    @Test
    public void testCreateDataset() throws ExecutionException, InterruptedException {
        final ResourcePath dataset01name = ResourcePath.apply(
            ResourceName.apply("my-namespace"),
            ResourceName.apply("dataset-01"));

        final TestSetup setup = TestSetup
            .apply()
            .withNamespace(dataset01name.getNamespace().getValue());

        final DatasetDetails datasetDetails = setup.getApp()
                                                   .datasets()
                                                   .createDataset(setup.getDefaultUser(), dataset01name)
                                                   .toCompletableFuture()
                                                   .get();

        assertThat(datasetDetails.getDataset()).isEqualTo(dataset01name);
        assertThat(datasetDetails.getCreatedBy().getName()).isEqualTo(setup.getDefaultUser().getDisplayName());

        final NamespaceDetails namespaceDetails01 = setup
            .getApp()
            .namespaces()
            .getNamespaceDetails(setup.getDefaultUser(), dataset01name.getNamespace())
            .toCompletableFuture()
            .get();

        assertThat(namespaceDetails01.getDatasets()).hasSize(1);
        assertThat(namespaceDetails01.getDatasets()).contains(dataset01name.getName());

        setup
            .getApp()
            .datasets()
            .deleteDataset(setup.getDefaultUser(), dataset01name)
            .toCompletableFuture()
            .get();

        final NamespaceDetails namespaceDetails02 = setup
            .getApp()
            .namespaces()
            .getNamespaceDetails(setup.getDefaultUser(), dataset01name.getNamespace())
            .toCompletableFuture()
            .get();

        assertThat(namespaceDetails02.getDatasets()).hasSize(0);

        setup.getApp().terminate();
    }

    @Test
    public void testGrantAndRevoke() throws ExecutionException, InterruptedException {
        final String namespace = "namespace";
        final String dataset = "ds";

        // Given
        final TestSetup setup = TestSetup
            .apply()
            .withDataset(namespace, dataset);

        final User exec = setup.getDefaultUser();
        final ResourcePath datasetId = ResourcePath.apply(namespace, dataset);
        final RoleAuthorization authorization01 = RoleAuthorization.apply("foo");
        final RoleAuthorization authorization02 = RoleAuthorization.apply("bar");

        // When granting access to a dataset
        DatasetDetails details01 = setup
            .getApp()
            .datasets()
            .grantDatasetAccess(exec, datasetId, DatasetPrivilege.CONSUMER, authorization01)
            .toCompletableFuture()
            .get();

        // The list of grants should have a size of one and should include the grant
        assertThat(details01.getAcl().getGrants().size()).isEqualTo(1);
        DatasetGrant grant01 = details01.getAcl().getGrants().iterator().next();
        assertThat(grant01.getAuthorization().getBy().getId()).isEqualTo(setup.getDefaultUser().getUserId().getId());
        assertThat(grant01.getAuthorization().getAuthorization()).isEqualTo(authorization01);

        // When adding another grant
        DatasetDetails details02 = setup
            .getApp()
            .datasets()
            .grantDatasetAccess(exec, datasetId, DatasetPrivilege.PRODUCER, authorization01)
            .toCompletableFuture()
            .get();

        // The list of grants should increase by one
        assertThat(details02.getAcl().getGrants().size()).isEqualTo(2);

        // When adding the same grant again
        DatasetDetails details03 = setup
            .getApp()
            .datasets()
            .grantDatasetAccess(exec, datasetId, DatasetPrivilege.PRODUCER, authorization01)
            .toCompletableFuture()
            .get();

        // The list of grants should not be increased
        assertThat(details03.getAcl().getGrants().size()).isEqualTo(2);

        // We may also use other authorizations
        DatasetDetails details04 = setup
            .getApp()
            .datasets()
            .grantDatasetAccess(exec, datasetId, DatasetPrivilege.PRODUCER, authorization02)
            .toCompletableFuture()
            .get();

        assertThat(details04.getAcl().getGrants().size()).isEqualTo(3);

        // When revoking an authorization which is not included, nothing should happen
        DatasetDetails details05 = setup
            .getApp()
            .datasets()
            .revokeDatasetAccess(exec, datasetId, DatasetPrivilege.ADMIN, authorization02)
            .toCompletableFuture()
            .get();

        assertThat(details05.getAcl().getGrants().size()).isEqualTo(3);

        // But when revoking an existing authorization, it should not be contained anymore
        DatasetDetails details06 = setup
            .getApp()
            .datasets()
            .revokeDatasetAccess(exec, datasetId, DatasetPrivilege.PRODUCER, authorization02)
            .toCompletableFuture()
            .get();

        assertThat(details06.getAcl().getGrants().size()).isEqualTo(2);

        setup.getApp().terminate();
    }

    @Test
    public void testAccessControl() throws Throwable {
        final String namespace = "namespace";
        final String dataset = "ds";

        /*
         * Given a namespace with a dataset created by a default user
         */
        final TestSetup setup = TestSetup
            .apply()
            .withDataset(namespace, dataset);

        final AuthenticatedUser someUser = setup.getDefaultUser();
        final AuthenticatedUser otherUser = setup.getOtherUser();
        final ResourcePath datasetResource = ResourcePath.apply(namespace, dataset);
        final ThrowableAssert.ThrowingCallable someAction = () -> setup
            .getApp()
            .datasets()
            .getDetails(otherUser, datasetResource)
            .toCompletableFuture()
            .get();

        /*
         * Then another user has not access to that dataset
         */
        assertThatThrownBy(someAction)
            .hasMessageContaining("not authorized");

        /*
         * When the owner grants access to the other user it has access.
         */
        setup
            .getApp()
            .datasets()
            .grantDatasetAccess(
                someUser,
                datasetResource,
                DatasetPrivilege.CONSUMER,
                UserAuthorization.apply(otherUser))
            .toCompletableFuture()
            .get();

        someAction.call();

        /*
         * When the owner revokes the access, again the user doesn't have access
         */
        setup
            .getApp()
            .datasets()
            .revokeDatasetAccess(
                someUser,
                datasetResource,
                DatasetPrivilege.CONSUMER,
                UserAuthorization.apply(otherUser))
            .toCompletableFuture()
            .get();

        assertThatThrownBy(someAction)
            .hasMessageContaining("not is authorized");

        /*
         * When the owner grants access to the whole namespace, the user also has access to the dataset
         */
        setup
            .getApp()
            .namespaces()
            .grantNamespaceAccess(
                someUser,
                datasetResource.getNamespace(),
                NamespacePrivilege.MEMBER,
                UserAuthorization.apply(otherUser))
            .toCompletableFuture()
            .get();

        someAction.call();

        /*
         * Revoking namespace access also revokes access to dataset
         */
        setup
            .getApp()
            .namespaces()
            .revokeNamespaceAccess(
                someUser,
                datasetResource.getNamespace(),
                NamespacePrivilege.MEMBER,
                UserAuthorization.apply(otherUser))
            .toCompletableFuture()
            .get();

        assertThatThrownBy(someAction)
            .hasMessageContaining("not authorized");

        /*
         * Granting access for a role allows all users of the role to access datasets
         */
        setup
            .getApp()
            .namespaces()
            .grantNamespaceAccess(
                someUser,
                datasetResource.getNamespace(),
                NamespacePrivilege.MEMBER,
                RoleAuthorization.apply(setup.getCommonRole()))
            .toCompletableFuture()
            .get();

        someAction.call();

        setup.getApp().terminate();
    }

    @Test
    public void testDatasetCreationOwnership() throws ExecutionException, InterruptedException {
        final String namespace = "namespace";
        final String dataset = "ds";
        final ResourcePath datasetResource = ResourcePath.apply(namespace, dataset);

        /*
         * Given a namespace which is owned by a role authorization
         */
        final TestSetup setup = TestSetup.apply();
        final RoleAuthorization owner = RoleAuthorization.apply(setup.getCommonRole());

        setup
            .withNamespace(namespace)
            .withNamespaceOwner(namespace, owner);

        final AuthenticatedUser user = setup.getOtherUser();

        /*
         * When a user with the role creates a dataset, the owner of the dataset
         * should be the same as the owner of the namespace.
         */
        DatasetDetails datasetDetails01 = setup
            .getApp()
            .datasets()
            .createDataset(user, datasetResource)
            .toCompletableFuture()
            .get();

        assertThat(datasetDetails01.getAcl().getOwner().getAuthorization()).isEqualTo(owner);

        /*
         * Nevertheless the ownership of a dataset can be explicitly set.
         */
        UserAuthorization newOwner = UserAuthorization.apply(user);
        DatasetDetails datasetDetails02 = setup
            .getApp()
            .datasets()
            .changeOwner(user, datasetResource, newOwner)
            .toCompletableFuture()
            .get();

        assertThat(datasetDetails02.getAcl().getOwner().getAuthorization()).isEqualTo(newOwner);

        setup.getApp().terminate();
    }

    @Test
    public void testNotExistingDataset() {
        final String namespace = "namespace";
        final String dataset = "ds";
        final ResourcePath datasetResource = ResourcePath.apply(namespace, dataset);

        /*
         * Given a namespace which is doesn't contain any dataset
         */
        final TestSetup setup = TestSetup.apply();
        final AuthenticatedUser user = setup.getDefaultUser();

        /*
         * When accessing a non-existing dataset we expect an exception
         */
        assertThatThrownBy(
            () ->
                setup
                    .getApp()
                    .datasets()
                    .getDetails(user, datasetResource)
                    .toCompletableFuture()
                    .get()).hasMessageContaining("does not exist");

        setup.getApp().terminate();
    }

}
