# Workspaces

Workspaces are the main entity of Maquette. All resources (e.g., models, data assets, etc.) are created and managed within a workspace. A workspace can have multiple members which work together in the workspace.

## RBAC Roles

The following roles are defined for workspaces.

**/maquette/workspaces/roles/contributor**

This role provides access to work with a workspace.

Included permissions:

* `/maquette/workspace/permissions/read`

<hr>

**/maquette/workspaces/roles/registered-user**

This role contains all permissions which should be assigned to registered users.

Included permissions:

* `/maquette/workspaces/permissions/create`

<hr>

**/maquette/workspaces/roles/owner**

Provides management and administration access for a workspace.

Included permissions:

* `/maquette/workspace/permissions/manage`
* `/maquette/workspace/permissions/read`

## RBAC Permissions

The following permissions are defined for workspaces.

**/maquette/workspaces/permissions/create** 

Allows a user to create workspaces within Maquette. The user will become the owner of the new workspace.

Parameters: `<NONE>`

<hr>

**/maquette/workspace/permissions/manage** 

Allows a user to manage a workspace, including rename, delete, modify members, etc..

Parameters:

* `workspace` - The Id of the workspace.

<hr>

**/maquette/workspace/permissions/read** 

Allows a user to view a workspaces content.

Parameters:

* `workspace` - The Id of the workspace.