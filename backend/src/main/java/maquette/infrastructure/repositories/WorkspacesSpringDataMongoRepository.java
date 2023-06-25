package maquette.infrastructure.repositories;

import maquette.core.domain.workspaces.Workspace;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WorkspacesSpringDataMongoRepository extends MongoRepository<Workspace, String> {

    Optional<Workspace> findOneWorkspaceByName(String name);

}
