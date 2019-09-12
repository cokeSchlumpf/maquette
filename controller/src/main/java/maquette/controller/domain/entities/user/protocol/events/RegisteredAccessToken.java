package maquette.controller.domain.entities.user.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.user.protocol.UserEvent;
import maquette.controller.domain.values.iam.Token;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisteredAccessToken implements UserEvent {

    private final Token token;

    @JsonCreator
    public static RegisteredAccessToken apply(
        @JsonProperty("token") Token token) {

        return new RegisteredAccessToken(token);
    }

}
