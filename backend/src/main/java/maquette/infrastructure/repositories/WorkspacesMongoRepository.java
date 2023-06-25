package maquette.infrastructure.repositories;

import lombok.AllArgsConstructor;
import maquette.core.domain.workspaces.Workspace;
import maquette.core.domain.workspaces.WorkspacesRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class WorkspacesMongoRepository implements WorkspacesRepository {

    private final WorkspacesSpringDataMongoRepository spring;

    @Override
    public Optional<Workspace> findWorkspaceByName(String name) {
        return spring.findOneWorkspaceByName(name);
    }

    @Override
    public void insertOrUpdate(Workspace workspace) {
        spring.save(workspace);
    }

}
