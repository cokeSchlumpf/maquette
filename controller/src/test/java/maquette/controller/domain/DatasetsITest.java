package maquette.controller.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetGrant;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.iam.RoleAuthorization;
import maquette.controller.domain.values.iam.User;
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

        setup
            .getApp()
            .datasets();
    }

}
