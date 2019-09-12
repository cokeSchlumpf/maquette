package maquette.controller.domain.values.iam;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.UID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Token {

    private final UID secret;

    private final TokenDetails details;

    @JsonCreator
    public static Token apply(
        @JsonProperty("secret") UID secret,
        @JsonProperty("details") TokenDetails details) {

        return new Token(secret, details);
    }

}
