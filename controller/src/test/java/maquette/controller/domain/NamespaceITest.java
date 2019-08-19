package maquette.controller.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.AnonymousUser;
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
    public void testCreateAndList() throws ExecutionException, InterruptedException {
        CoreApplication app = CoreApplication.apply();

        ResourceName ns01 = ResourceName.apply("my-namespace");
        ResourceName ns02 = ResourceName.apply("other-namespace");
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

        Set<NamespaceInfo> namespaceInfos = app
            .namespaces()
            .listNamespaces(user)
            .toCompletableFuture()
            .get();

        app.terminate();

        assertThat(namespaceInfos).hasSize(2);
    }

}
