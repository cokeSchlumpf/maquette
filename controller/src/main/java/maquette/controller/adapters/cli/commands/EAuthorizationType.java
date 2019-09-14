package maquette.controller.adapters.cli.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

}
