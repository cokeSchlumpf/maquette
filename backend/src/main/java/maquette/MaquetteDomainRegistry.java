package maquette;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.domain.users.RegisteredUsersRepository;
import maquette.core.domain.workspaces.WorkspacesRepository;

@Value
@AllArgsConstructor(staticName = "apply")
public class MaquetteDomainRegistry {

    RegisteredUsersRepository users;

    WorkspacesRepository workspaces;

    ObjectMapper objectMapper;

}
