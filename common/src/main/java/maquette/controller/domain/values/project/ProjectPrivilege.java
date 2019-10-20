package maquette.controller.domain.values.project;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectPrivilege {

    MEMBER("member"),
    PRODUCER("producer"),
    CONSUMER("consumer"),
    ADMIN("admin");

    private static Map<String, ProjectPrivilege> namesMap = new HashMap<>(4);

    public final String name;

    ProjectPrivilege(String name) {
        this.name = name;
    }

    static {
        namesMap.put(MEMBER.name, MEMBER);
        namesMap.put(PRODUCER.name, PRODUCER);
        namesMap.put(CONSUMER.name, CONSUMER);
        namesMap.put(ADMIN.name, ADMIN);
    }

    @JsonCreator
    private static ProjectPrivilege forValue(String value) {
        return namesMap.get(StringUtils.lowerCase(value));
    }

    @JsonValue
    private String toValue() {
        return name;
    }

}
