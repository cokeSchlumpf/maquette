package maquette.controller.domain.values.core;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.exceptions.InvalidResourceNameException;
import maquette.controller.domain.util.Operators;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResourcePath {

    private final ResourceName namespace;

    private final ResourceName name;

    @JsonCreator
    public static ResourcePath apply(
        @JsonProperty("namespace") ResourceName namespace,
        @JsonProperty("name") ResourceName name) {

        return new ResourcePath(namespace, name);
    }

    public static ResourcePath apply(ResourceName namespace) {
        return apply(namespace, null);
    }

    public static ResourcePath apply(String s) {
        try {
            String[] parts = s.split("/");

            if (parts.length > 1) {
                ResourceName namespace = ResourceName.apply(parts[0]);
                ResourceName name = ResourceName.apply(parts[1]);

                return apply(namespace, name);
            } else {
                return apply(ResourceName.apply(s));
            }
        } catch (Exception e) {
            throw InvalidResourceNameException.apply(s);
        }
    }

    public static Optional<ResourcePath> tryApply(String s) {
        return Operators.exceptionToNone(() -> apply(s));
    }

    public Optional<ResourceName> getName() {
        return Optional.ofNullable(name);
    }

    @Override
    public String toString() {
        return String.format("%s/%s", namespace, name);
    }

}
