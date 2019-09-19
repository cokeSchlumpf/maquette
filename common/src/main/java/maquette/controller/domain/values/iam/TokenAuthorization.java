package maquette.controller.domain.values.iam;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.UID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenAuthorization implements  Authorization {

    private static final String TOKEN = "token";

    @JsonProperty(TOKEN)
    private final UID token;

    @JsonCreator
    public static TokenAuthorization apply(
        @JsonProperty(TOKEN) UID token) {

        return new TokenAuthorization(token);
    }

    @Override
    public boolean hasAuthorization(User user) {
        if (user instanceof TokenAuthenticatedUser) {
            return hasAuthorization((TokenAuthenticatedUser) user);
        } else {
            return false;
        }
    }

    private boolean hasAuthorization(TokenAuthenticatedUser user) {
        return user.getToken().getId().equals(token);
    }

    public String toString() {
        return String.format("token(%s)", token);
    }

}
