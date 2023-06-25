# Role-based access control

Maquette implements Role-based access control concepts which allows to assign users fine-graned access permissions to user. The main classes to implement these concepts are `maquette.core.domain.users.User`, `maquette.core.domain.users.rbac.DomainRole` and `maquette.core.domain.users.rbac.DomainPermission`.

![RBAC Entities](./resources/rbac-user-classes.png)

As shown in the sketch, a user has a set of assigned roles, each roles consists of a set of permissions. When checking authorization for an action, one can call `hasPermission` on a user.

DomainPermissions are implemented as value classes, e.g. `maquette.core.domain.workspaces.rbac.WorkspacePermissions.CreateWorkspaces`. Each permission specifies a unique name which is usually some kind of an hierarchical identifier to avoid name conflicts across contexts. For example, the name of `CreateWorkspaces` is `/maquette/workspaces/permissions/create`.

```java
public final class WorkspacePermissions {

    private WorkspacePermissions() {

    }

    @Value
    public static class CreateWorkspaces implements DomainPermission {

        public static final String NAME = "/maquette/workspaces/permissions/create";

    }

    public record ManageWorkspace(String workspace) implements DomainPermission {

        public static final String NAME = "/maquette/workspace/permissions/manage";

    }

}
```

Permissions might have additional parameters, like in the example above `ManageWorkspace` has a property `workspace` to specify the specific workspace for which the permission is valid.

DomainRoles are similar to permissions, they help to bundle multiple fine grained permissions to more usage oriented roles. They are also implemented as value classes and also have an hierarchical name.

```java
@Value
@EqualsAndHashCode(callSuper = false)
public class WorkspaceOwnerRole implements DomainRole {

    String workspace;

    public static String NAME = "/maquette/workspaces/roles/owner";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Set<DomainPermission> getPermissions() {
        return Set.of(
            new WorkspacePermissions.ManageWorkspace(workspace),
            new WorkspacePermissions.ReadWorkspace(workspace)
        );
    }


}
```

The set of related permissions is hard coded and should not be configirable. Like permissions, also roles might have additional properties to identify their specific scope (e.g., `workspace`).

> **Note:** Custom Roles are planned for the future to allow customized roles.

## Default Roles

To allow default roles on global scopes (e.g., all workspaces, all models, etc. ...), the application configuration (`maquette.core.application.MaquetteApplicationConfiguration`) defines a set of domain roles for registered users.

Within the application configuration, the set of default roles can be specified using the name of the roles. For example:

```yaml
maquette:
  registered-users-default-roles:
    - /maquette/workspaces/roles/registered-user
```

The mapping from the configuration values to the actual Roles is done by `maquette.infrastructure.DomainRoleSpringConfigurationConverter`. 

> **Note:** As of now only roles without additional attributes can be configured. 