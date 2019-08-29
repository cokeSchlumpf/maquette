package maquette.util;

import lombok.AllArgsConstructor;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(staticName = "apply")
public class TestSetup {

    private final CoreApplication app;

    private final AuthenticatedUser defaultUser;

    private final AuthenticatedUser otherUser;

    public static TestSetup apply() {
        CoreApplication app = CoreApplication.apply();
        AuthenticatedUser defaultUser = AuthenticatedUser.apply("hippo", "Hippo Ewen-Wellner");
        AuthenticatedUser otherUser = AuthenticatedUser.apply("egon", "Egon Olsen");
        return TestSetup.apply(app, defaultUser, otherUser);
    }

    public CoreApplication getApp() {
        return app;
    }

    public AuthenticatedUser getDefaultUser() {
        return defaultUser;
    }

    public AuthenticatedUser getOtherUser() {
        return otherUser;
    }

    public TestSetup withDataset(String namespace, String name) {
        return withDataset(ResourcePath.apply(namespace, name), defaultUser);
    }

    public TestSetup withDataset(String namespace, String name, User executor) {
        return withDataset(ResourcePath.apply(namespace, name), executor);
    }

    public TestSetup withDataset(ResourcePath path, User executor) {
        withNamespace(path.getNamespace().getValue(), executor);

        Operators.suppressExceptions(() -> {
            app
                .datasets()
                .createDataset(executor, path)
                .toCompletableFuture()
                .get();
        });

        return this;
    }

    public TestSetup withNamespace(String name, User executor) {
        Operators.suppressExceptions(() -> {
            app
                .namespaces()
                .createNamespace(executor, ResourceName.apply(name))
                .toCompletableFuture()
                .get();
        });

        return this;
    }

    public TestSetup withNamespace(String name) {
        withNamespace(name, defaultUser);
        return this;
    }

}
