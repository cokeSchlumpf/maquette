package maquette.core.domain.workspaces;

import java.util.Optional;

public interface WorkspacesReadRepository {

    Optional<Workspace> findWorkspaceByName(String name);

}
