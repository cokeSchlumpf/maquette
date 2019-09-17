package maquette.controller.adapters.cli.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.RoleAuthorization;
import maquette.controller.domain.values.iam.UserAuthorization;
import maquette.controller.domain.values.iam.WildcardAuthorization;

public enum EAuthorizationType {

    USER("user"),
    ROLE("role"),
    WILDCARD("*");

    private final String value;

    EAuthorizationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public Authorization asAuthorization(String to) {
        switch (this) {
            case USER:
                return UserAuthorization.apply(to);
            case ROLE:
                return RoleAuthorization.apply(to);
            case WILDCARD:
                return WildcardAuthorization.apply();
            default:
                throw new IllegalArgumentException("Unknown authorization type");
        }
    }

}
