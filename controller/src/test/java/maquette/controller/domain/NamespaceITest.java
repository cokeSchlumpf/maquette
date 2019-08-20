package maquette.controller.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespaceInfo;

public class NamespaceITest {

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
        CoreApplication app = CoreApplication.apply();

        ResourceName ns01 = ResourceName.apply("my-namespace");
        ResourceName ns02 = ResourceName.apply("other-namespace");
        ResourceName ns03 = ResourceName.apply("some-namespace");
        User user = AuthenticatedUser.apply("mw", "mw");

        app
            .namespaces()
            .createNamespace(user, ns01)
            .toCompletableFuture()
            .get();

        app
            .namespaces()
            .createNamespace(user, ns02)
            .toCompletableFuture()
            .get();

        app
            .namespaces()
            .createNamespace(user, ns03)
            .toCompletableFuture()
            .get();

        Set<NamespaceInfo> namespaceInfos01 = app
            .namespaces()
            .listNamespaces(user)
            .toCompletableFuture()
            .get();

        assertThat(namespaceInfos01).hasSize(3);

        /*
         * Delete a namespace and get list of namespaces.
         */
        app
            .namespaces()
            .deleteNamespace(user, ns01)
            .toCompletableFuture()
            .get();

        app
            .namespaces()
            .deleteNamespace(user, ns02)
            .toCompletableFuture()
            .get();

        Set<NamespaceInfo> namespaceInfos02 = app
            .namespaces()
            .listNamespaces(user)
            .toCompletableFuture()
            .get();

        assertThat(namespaceInfos02).hasSize(1);

        app.terminate();

    }

}
