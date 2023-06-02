package maquette.core.domain.workspaces.events;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.domain.workspaces.Workspace;

@Value
@AllArgsConstructor(staticName = "apply")
public class WorkspaceCreatedEvent {

    Workspace workspace;

}
