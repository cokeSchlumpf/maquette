package maquette.controller.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.ExecutionException;

import org.apache.avro.Schema;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import com.google.common.collect.Lists;

import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.records.Records;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetGrant;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.dataset.VersionInfo;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.RoleAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserAuthorization;
import maquette.controller.domain.values.namespace.NamespaceDetails;
import maquette.controller.domain.values.namespace.NamespacePrivilege;
import maquette.util.CountryTestData;
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
        assertThat(datasetDetails.getCreatedBy().getId()).isEqualTo(setup.getDefaultUser().getId());

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
            .hasMessageContaining("not authorized");

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

    @Test
    public void testData() throws ExecutionException, InterruptedException {
        final String namespace = "namespace";
        final String dataset = "ds";
        final ResourcePath datasetResource = ResourcePath.apply(namespace, dataset);

        final Schema schema = CountryTestData.getSchema();
        final Records records = Records.fromRecords(CountryTestData.getRecords());

        TestSetup setup = TestSetup
            .apply()
            .withNamespace(namespace)
            .withDataset(namespace, dataset);

        User user = setup.getDefaultUser();

        /*
         * Create a new version in the dataset
         */
        UID uid1 = setup
            .getApp()
            .datasets()
            .createDatasetVersion(user, datasetResource, schema)
            .toCompletableFuture()
            .get();

        /*
         * Details should reflect that ...
         */
        DatasetDetails d01 = setup
            .getApp()
            .datasets()
            .getDetails(user, datasetResource)
            .toCompletableFuture()
            .get();

        assertThat(d01.getVersions()).hasSize(1);

        VersionInfo v01 = Lists.newArrayList(d01.getVersions()).get(0);
        assertThat(v01.getVersionId()).isEqualTo(uid1);
        assertThat(v01.getRecords()).isEqualTo(0);

        /*
         * Inserting Data into the Dataset ...
         */
        VersionDetails v02 = setup
            .getApp()
            .datasets()
            .pushData(user, datasetResource, uid1, records.getSource())
            .toCompletableFuture()
            .get();

        assertThat(v02.getVersionId()).isEqualTo(uid1);
        assertThat(v02.getRecords()).isEqualTo(records.size());

        /*
         * Committing and publishing the dataset.
         */
        VersionTag vt01 = setup
            .getApp()
            .datasets()
            .publishDatasetVersion(user, datasetResource, uid1, "Some nice commit")
            .toCompletableFuture()
            .get();

        assertThat(vt01).isEqualTo(VersionTag.apply(1, 0, 0));

        /*
         * Now we can download the data from the dataset
         */
        Records d1 = setup
            .getApp()
            .datasets()
            .getData(user, datasetResource)
            .toCompletableFuture()
            .get();

        assertThat(d1.getRecords())
            .hasSize(records.size())
            .containsAll(records.getRecords());

        /*
         * Adding new data to a committed dataset should not be possible.
         */
        assertThatThrownBy(() -> setup
            .getApp()
            .datasets()
            .pushData(user, datasetResource, uid1, records.getSource())
            .toCompletableFuture()
            .get()).hasMessageContaining("already committed");

        /*
         * No we create another version.
         */
        UID uid2 = setup
            .getApp()
            .datasets()
            .createDatasetVersion(user, datasetResource, schema)
            .toCompletableFuture()
            .get();

        setup
            .getApp()
            .datasets()
            .pushData(user, datasetResource, uid2, records.getSource())
            .toCompletableFuture()
            .get();

        setup
            .getApp()
            .datasets()
            .pushData(user, datasetResource, uid2, records.getSource())
            .toCompletableFuture()
            .get();

        DatasetDetails v03 = setup
            .getApp()
            .datasets()
            .getDetails(user, datasetResource)
            .toCompletableFuture()
            .get();

        assertThat(v03.getVersions()).hasSize(2);

        /*
         * Commit and fetch data of latest version
         */
        setup
            .getApp()
            .datasets()
            .publishDatasetVersion(user, datasetResource, uid2, "Some new data")
            .toCompletableFuture()
            .get();

        Records d2 = setup
            .getApp()
            .datasets()
            .getData(user, datasetResource)
            .toCompletableFuture()
            .get();

        assertThat(d2.getRecords()).hasSize(records.size() * 2);

        /*
         * Fetch data of specified version
         */
        Records d3 = setup
            .getApp()
            .datasets()
            .getData(user, datasetResource, VersionTag.apply("1.0.0"))
            .toCompletableFuture()
            .get();

        assertThat(d3.size()).isEqualTo(records.size());

        VersionDetails vd04 = setup
            .getApp()
            .datasets()
            .getVersionDetails(user, datasetResource)
            .toCompletableFuture()
            .get();

        VersionDetails vd05 = setup
            .getApp()
            .datasets()
            .getVersionDetails(user, datasetResource, VersionTag.apply("1.0.0"))
            .toCompletableFuture()
            .get();

        assertThat(vd04.getRecords()).isEqualTo(records.size() * 2);
        assertThat(vd05.getRecords()).isEqualTo(records.size());

        setup.getApp().terminate();
    }

}
