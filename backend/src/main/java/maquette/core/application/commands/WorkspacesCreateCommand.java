package maquette.core.application.commands;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import maquette.MaquetteDomainRegistry;
import maquette.common.commands.Command;
import maquette.common.commands.MessageResult;
import maquette.core.domain.users.User;
import maquette.core.domain.workspaces.Workspace;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class WorkspacesCreateCommand implements Command<MessageResult<Workspace>> {

    @NotBlank
    String name;

    String description;

    @Override
    public MessageResult<Workspace> run(User user, MaquetteDomainRegistry domainRegistry) {
        var workspace = Workspace.fromInitialSettings(user, name, description);
        workspace.create(user, domainRegistry.getWorkspaces());

        return MessageResult
            .formatted("Successfully created workspace `%s`", workspace)
            .withData(workspace);
    }

}
