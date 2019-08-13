package maquette.controller.domain.values.iam;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GrantedAuthorization {

    User by;

    Date at;

    Authorization authorization;

    @JsonCreator
    public static GrantedAuthorization apply(
        @JsonProperty("by") User by,
        @JsonProperty("at") Date at,
        @JsonProperty("authorization") Authorization authorization) {

        return new GrantedAuthorization(by, at, authorization);
    }

}
