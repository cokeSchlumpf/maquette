package maquette.controller.domain.values.iam;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GrantedAuthorization {

    UserId by;

    Instant at;

    Authorization authorization;

    @JsonCreator
    public static GrantedAuthorization apply(
        @JsonProperty("by") UserId by,
        @JsonProperty("at") Instant at,
        @JsonProperty("authorization") Authorization authorization) {

        return new GrantedAuthorization(by, at, authorization);
    }

}
