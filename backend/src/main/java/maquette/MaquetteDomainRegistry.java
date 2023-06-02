package maquette;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.domain.workspaces.WorkspacesRepository;

@Value
@AllArgsConstructor(staticName = "apply")
public class MaquetteDomainRegistry {

    WorkspacesRepository workspaces;

}
