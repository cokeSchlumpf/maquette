package maquette.core.domain.workspaces;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import maquette.common.AggregateRoot;
import maquette.common.Operators;
import maquette.core.domain.users.User;
import maquette.core.domain.values.ActionMetadata;
import maquette.core.domain.workspaces.events.WorkspaceCreatedEvent;
import maquette.core.domain.workspaces.exceptions.WorkspaceAlreadyExistsException;
import maquette.core.domain.workspaces.rbac.WorkspacePermissions;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Workspace extends AggregateRoot<String, Workspace> {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String CREATED = "created";
    private static final String MODIFIED = "modified";

    @JsonProperty(ID)
    private final String id;

    @JsonProperty(NAME)
    private String name;

    @JsonProperty(DESCRIPTION)
    private String description;

    @JsonProperty(CREATED)
    private ActionMetadata created;

    @JsonProperty(MODIFIED)
    private ActionMetadata modified;

    @JsonCreator
    private static Workspace apply(
        @JsonProperty(ID) final String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(CREATED) ActionMetadata created,
        @JsonProperty(MODIFIED) ActionMetadata modified
    ) {
        return new Workspace(id, name, description, created, modified);
    }

    public static Workspace fake(String name) {
        return apply(
            "123", name, "Lorem ipsum",
            ActionMetadata.apply("alice"), ActionMetadata.apply("bob")
        );
    }

    public static Workspace fromInitialSettings(User creator, String name, String description) {
        var id = Operators.randomHash();
        var created = ActionMetadata.apply(creator);

        return new Workspace(id, name, description, created,created);
    }

    /**
     * Creates (persists) this workspace in the database.
     *
     * @param executor The user executing the action.
     * @param workspaces The repository to store the data.
     */
    public void create(User executor, WorkspacesRepository workspaces) {
        var maybeExistingWorkspace = workspaces.findWorkspaceByName(this.name);

        if (maybeExistingWorkspace.isPresent()) {
            var existingWorkspace = maybeExistingWorkspace.get();

            if (!existingWorkspace.getCreated().getBy().equals(executor.getDisplayName())) {
                throw WorkspaceAlreadyExistsException.apply(name);
            } else {
                // Workspace already exists.
                return;
            }
        }

        // Check Permissions
        executor.requirePermission(new WorkspacePermissions.CreateWorkspaces());

        // TODO: Add Bean Validation
        workspaces.insertOrUpdate(this);
        registerEvent(WorkspaceCreatedEvent.apply(this));
    }

}
