package maquette.controller.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import maquette.controller.adapters.InMemoryDataStorageAdapter;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.RoleAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserAuthorization;
import maquette.controller.domain.values.namespace.NamespaceInfo;
import maquette.controller.domain.values.namespace.NamespacePrivilege;
import maquette.util.TestSetup;

public class NamespacesITest {

    @Test
    public void testCreateNamespace() throws ExecutionException, InterruptedException {
        CoreApplication app = CoreApplication.apply(InMemoryDataStorageAdapter.apply());

        ResourceName namespaceName = ResourceName.apply("my-namespace");
        User user = AuthenticatedUser.apply("mw", "mw");

        NamespaceInfo namespaceInfo = app
            .namespaces()
            .createNamespace(user, namespaceName)
            .toCompletableFuture()
            .get();

        assertThat(namespaceInfo).isNotNull();
        assertThat(namespaceInfo.getName()).isEqualTo(namespaceName);

        app.terminate();
    }

    @Test
    public void testCreateListAndDelete() throws ExecutionException, InterruptedException {
        TestSetup setup = TestSetup
            .apply()
            .withNamespace("my-namespace")
            .withNamespace("other-namespace")
            .withNamespace("some-namespace");

        /*
         * List namespaces
         */
        Set<NamespaceInfo> namespaceInfos01 = setup
            .getApp()
            .namespaces()
            .listNamespaces(setup.getDefaultUser())
            .toCompletableFuture()
            .get();

        assertThat(namespaceInfos01).hasSize(3);

        /*
         * Delete a namespaces and get list of namespaces.
         */
        setup.getApp()
             .namespaces()
             .deleteNamespace(setup.getDefaultUser(), ResourceName.apply("my-namespace"))
             .toCompletableFuture()
             .get();

        setup.getApp()
             .namespaces()
             .deleteNamespace(setup.getDefaultUser(), ResourceName.apply("other-namespace"))
             .toCompletableFuture()
             .get();

        Set<NamespaceInfo> namespaceInfos02 = setup.getApp()
                                                   .namespaces()
                                                   .listNamespaces(setup.getDefaultUser())
                                                   .toCompletableFuture()
                                                   .get();

        assertThat(namespaceInfos02).hasSize(1);

        setup.getApp().terminate();

    }

    @Test
    public void testListNamespaces() throws Exception {
        final String namespace = "my-namespace";

        // Given an existing namespace owned by some user
        final TestSetup setup = TestSetup
            .apply()
            .withNamespace(namespace);
        final AuthenticatedUser someUser = setup.getDefaultUser();

        // ... and another user
        final AuthenticatedUser otherUser = setup.getOtherUser();

        // Then the owner of the namespace should receive his namespaces when requesting a list
        Set<NamespaceInfo> namespaces01 = setup
            .getApp()
            .namespaces()
            .listNamespaces(someUser)
            .toCompletableFuture()
            .get();

        assertThat(namespaces01).hasSize(1);

        // And the other user shouldn't see it
        Set<NamespaceInfo> namespaces02 = setup
            .getApp()
            .namespaces()
            .listNamespaces(otherUser)
            .toCompletableFuture()
            .get();


        assertThat(namespaces02).hasSize(0);

        // When the first user grants access to the repository to the other user
        setup
            .getApp()
            .namespaces()
            .grantNamespaceAccess(
                someUser, ResourceName.apply(namespace),
                NamespacePrivilege.CONSUMER, UserAuthorization.apply(otherUser))
            .toCompletableFuture()
            .get();

        // Then the other user should see the namespace as well.
        Set<NamespaceInfo> namespaces03 = setup
            .getApp()
            .namespaces()
            .listNamespaces(otherUser)
            .toCompletableFuture()
            .get();


        assertThat(namespaces03).hasSize(1);

        // When the access is revoked
        setup
            .getApp()
            .namespaces()
            .revokeNamespaceAccess(
                someUser, ResourceName.apply(namespace),
                NamespacePrivilege.CONSUMER, UserAuthorization.apply(otherUser))
            .toCompletableFuture()
            .get();

        // Then the other user shouldn't see the namespace again.
        Set<NamespaceInfo> namespaces04 = setup
            .getApp()
            .namespaces()
            .listNamespaces(otherUser)
            .toCompletableFuture()
            .get();


        assertThat(namespaces04).hasSize(0);

        setup.getApp().terminate();
    }

    @Test
    public void testAccess() throws Exception {
        final String namespace = "my-namespace";

        // Given an existing namespace owned by some user
        final TestSetup setup = TestSetup
            .apply()
            .withNamespace(namespace);
        final AuthenticatedUser someUser = setup.getDefaultUser();

        // ... and another user
        final AuthenticatedUser otherUser = setup.getOtherUser();

        ResourceName namespaceResource = ResourceName.apply(namespace);
        assertThatThrownBy(() -> setup
            .getApp()
            .namespaces()
            .getNamespaceDetails(otherUser, namespaceResource)
            .toCompletableFuture()
            .get())
            .hasMessageContaining("not authorized");

        // When the first user grants access to the repository to the other user
        setup
            .getApp()
            .namespaces()
            .grantNamespaceAccess(
                someUser, namespaceResource,
                NamespacePrivilege.MEMBER, UserAuthorization.apply(otherUser))
            .toCompletableFuture()
            .get();

        // Then the action shouldn't throw an exception
        setup
            .getApp()
            .namespaces()
            .getNamespaceDetails(otherUser, namespaceResource)
            .toCompletableFuture()
            .get();

        // When the first user revokes access to the repository from the other user
        setup
            .getApp()
            .namespaces()
            .revokeNamespaceAccess(
                someUser, namespaceResource,
                NamespacePrivilege.MEMBER, UserAuthorization.apply(otherUser))
            .toCompletableFuture()
            .get();

        // Then the exception should be thrown again
        assertThatThrownBy(() -> setup
            .getApp()
            .namespaces()
            .getNamespaceDetails(otherUser, namespaceResource)
            .toCompletableFuture()
            .get())
            .hasMessageContaining("not authorized");

        // The owner also may give the ownership to a role
        setup
            .getApp()
            .namespaces()
            .changeOwner(someUser, namespaceResource, RoleAuthorization.apply(setup.getCommonRole()))
            .toCompletableFuture()
            .get();

        // Then the other user also has the right to do admin actions and see details as well
        setup
            .getApp()
            .namespaces()
            .getNamespaceDetails(otherUser, namespaceResource)
            .toCompletableFuture()
            .get();

        setup
            .getApp()
            .namespaces()
            .grantNamespaceAccess(otherUser, namespaceResource, NamespacePrivilege.PRODUCER, RoleAuthorization.apply("foo"))
            .toCompletableFuture()
            .get();

        setup.getApp().terminate();
    }

    @Test
    public void testAccessToNonExistingNamespace() throws ExecutionException, InterruptedException {
        // Given an empty app
        final TestSetup setup = TestSetup.apply();

        // Then requests to a non-existing namespace should fail
        assertThatThrownBy(() -> {
            setup
                .getApp()
                .namespaces()
                .getNamespaceDetails(setup.getDefaultUser(), ResourceName.apply("foo"))
                .toCompletableFuture()
                .get();
        }).hasMessageContaining("does not exist");

        setup.getApp().terminate();
    }

}
