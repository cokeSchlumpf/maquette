package maquette.core.domain.users.events;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.domain.users.RegisteredUser;

@Value
@AllArgsConstructor(staticName = "apply")
public class RegisteredUserRegisteredEvent {

    RegisteredUser user;

}
