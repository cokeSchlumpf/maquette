package maquette.util;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.adapters.storage.InMemoryDataStorageAdapter;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.ports.DataStorageAdapter;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(staticName = "apply")
public class TestSetup {

    private final CoreApplication app;

    private final String commonRole;

    private final AuthenticatedUser adminUser;

    private final AuthenticatedUser defaultUser;

    private final AuthenticatedUser otherUser;

    public static TestSetup apply(DataStorageAdapter storageAdapter) {
        CoreApplication app = CoreApplication.apply(storageAdapter);
        String commonRole = "common-role";
        AuthenticatedUser adminUser = AuthenticatedUser.apply("admin", "Administrator", commonRole, "admin");
        AuthenticatedUser defaultUser = AuthenticatedUser.apply("hippo", "Hippo Ewen-Wellner", commonRole);
        AuthenticatedUser otherUser = AuthenticatedUser.apply("egon", "Egon Olsen", commonRole);

        return TestSetup.apply(app, commonRole, adminUser, defaultUser, otherUser);
    }

    public static TestSetup apply() {
        return apply(InMemoryDataStorageAdapter.apply());
    }

    public TestSetup withDataset(String namespace, String name) {
        return withDataset(ResourcePath.apply(namespace, name), defaultUser);
    }

    public TestSetup withDataset(String namespace, String name, User executor) {
        return withDataset(ResourcePath.apply(namespace, name), executor);
    }

    public TestSetup withDataset(ResourcePath path, User executor) {
        withNamespace(path.getProject().getValue(), executor);

        return this;
    }

    public TestSetup withNamespace(String name, User executor) {
        return this;
    }

    public TestSetup withNamespace(String name) {
        withNamespace(name, defaultUser);
        return this;
    }

    public TestSetup withNamespaceOwner(String namespace, Authorization owner) {
        return withNamespaceOwner(namespace, owner, getDefaultUser());
    }

    public TestSetup withNamespaceOwner(String namespace, Authorization owner, User executor) {
        return this;
    }

}
