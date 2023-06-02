package maquette.core.domain.workspaces;

public interface WorkspacesRepository extends WorkspacesReadRepository {

    void insertOrUpdate(Workspace workspace);

}
