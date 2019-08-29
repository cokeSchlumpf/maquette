package maquette.controller.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespaceInfo;
import maquette.util.TestSetup;

public class NamespacesITest {

    @Test
    public void testCreateNamespace() throws ExecutionException, InterruptedException {
        CoreApplication app = CoreApplication.apply();

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

}
