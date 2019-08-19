package maquette.controller.domain;

import static org.assertj.core.api.Assertions.assertThat;

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
            .createNamespace(AnonymousUser.apply(), namespaceName)
            .toCompletableFuture()
            .get();

        assertThat(namespaceInfo).isNotNull();
        assertThat(namespaceInfo.getName()).isEqualTo(namespaceName);

        System.out.println(namespaceInfo);

        app.terminate();
    }

}
