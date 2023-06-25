package maquette.infrastructure;

import lombok.AllArgsConstructor;
import maquette.MaquetteDomainRegistry;
import maquette.common.commands.Command;
import maquette.common.commands.CommandResult;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MaquetteSpringCommandRunner {

    private final MaquetteDomainRegistry domainRegistry;

    private final MaquetteSpringUserContext userContext;

    public <T extends CommandResult> T run(Command<T> command) {
        return command.run(
            userContext.getUser(),
            domainRegistry
        );
    }

}
