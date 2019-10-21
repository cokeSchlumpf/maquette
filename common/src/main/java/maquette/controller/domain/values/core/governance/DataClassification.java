package maquette.controller.domain.values.core.governance;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DataClassification {

    OPEN("open"),
    SENSITIVE("sensitive"),
    PERSONAL_IDENTIFYING_INFORMATION("pii"),
    SENSITIVE_PERSONAL_INFORMATION("spi");

    private static Map<String, DataClassification> namesMap = new HashMap<>(4);

    public final String name;

    DataClassification(String name) {
        this.name = name;
    }

    static {
        namesMap.put(OPEN.name, OPEN);
        namesMap.put(SENSITIVE.name, SENSITIVE);
        namesMap.put(PERSONAL_IDENTIFYING_INFORMATION.name, PERSONAL_IDENTIFYING_INFORMATION);
        namesMap.put(SENSITIVE_PERSONAL_INFORMATION.name, SENSITIVE_PERSONAL_INFORMATION);
    }

    @JsonCreator
    private static DataClassification forValue(String value) {
        return namesMap.get(StringUtils.lowerCase(value));
    }

    @JsonValue
    private String toValue() {
        return name;
    }

}
