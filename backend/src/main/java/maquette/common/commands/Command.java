package maquette.common.commands;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import maquette.MaquetteDomainRegistry;
import maquette.core.domain.users.User;


@JsonIgnoreProperties({ "command" })
public interface Command<T extends CommandResult> {

    T run(User user, MaquetteDomainRegistry domainRegistry);

}
