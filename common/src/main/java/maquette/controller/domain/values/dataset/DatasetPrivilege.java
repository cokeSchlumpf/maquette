package maquette.controller.domain.values.dataset;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DatasetPrivilege {

    PRODUCER("producer"),
    CONSUMER("consumer"),
    ADMIN("admin");

    private static Map<String, DatasetPrivilege> namesMap = new HashMap<>(3);

    public final String name;

    DatasetPrivilege(String name) {
        this.name = name;
    }

    static {
        namesMap.put(PRODUCER.name, PRODUCER);
        namesMap.put(CONSUMER.name, CONSUMER);
        namesMap.put(ADMIN.name, ADMIN);
    }

    @JsonCreator
    private static DatasetPrivilege forValue(String value) {
        return namesMap.get(StringUtils.lowerCase(value));
    }

    @JsonValue
    private String toValue() {
        return name;
    }

}
