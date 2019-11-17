package maquette.controller.domain.values.iam;

import java.time.Instant;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.Markdown;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GrantedAuthorization {

    private static final String BY =" by";
    private static final String AT = "at";
    private static final String AUTHORIZATION = "authorization";

    @JsonProperty(BY)
    UserId by;

    @JsonProperty(AT)
    Instant at;

    @JsonProperty(AUTHORIZATION)
    Authorization authorization;

    @JsonCreator
    public static GrantedAuthorization apply(
        @JsonProperty(BY) UserId by,
        @JsonProperty(AT) Instant at,
        @JsonProperty(AUTHORIZATION) Authorization authorization) {

        return new GrantedAuthorization(by, at, authorization);
    }

}
