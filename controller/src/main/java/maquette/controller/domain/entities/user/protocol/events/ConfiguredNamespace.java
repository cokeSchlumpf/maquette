package maquette.controller.domain.entities.user.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.user.protocol.UserEvent;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.Token;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfiguredNamespace implements UserEvent {

    private final ResourceName namespace;

    @JsonCreator
    public static ConfiguredNamespace apply(
        @JsonProperty("namespace") ResourceName namespace) {

        return new ConfiguredNamespace(namespace);
    }

}
