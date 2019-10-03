package maquette.controller.domain.values.core;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.exceptions.InvalidResourceNameException;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonSerialize(using = ResourcePath.Serializer.class)
@JsonDeserialize(using = ResourcePath.Deserializer.class)
public final class ResourcePath {

    private final ResourceName namespace;

    private final ResourceName name;

    @JsonCreator
    public static ResourcePath apply(
        @JsonProperty("namespace") ResourceName namespace,
        @JsonProperty("name") ResourceName name) {

        return new ResourcePath(namespace, name);
    }

    public static ResourcePath apply(
        String namespace,
        String name) {
        return new ResourcePath(
            ResourceName.apply(namespace),
            ResourceName.apply(name));
    }

    public static ResourcePath apply(
        User executor,
        String namespace,
        String name) {

        return apply(Objects.isNull(namespace) || namespace.equals("_") ? executor.getUserId().getId() : namespace, name);
    }

    public static ResourcePath apply(
        User executor,
        ResourceName namespace,
        ResourceName name) {

        return apply(Objects.isNull(namespace) || namespace.getValue().equals("_") ? executor.getUserId().getId() : namespace.getValue(), name.getValue());
    }

    public static ResourcePath apply(String s) {
        try {
            String[] parts = s.split("/");

            if (parts.length == 2) {
                ResourceName namespace = ResourceName.apply(parts[0]);
                ResourceName name = ResourceName.apply(parts[1]);

                return apply(namespace, name);
            } else {
                throw InvalidResourceNameException.apply(s);
            }
        } catch (Exception e) {
            throw InvalidResourceNameException.apply(s);
        }
    }

    public static Optional<ResourcePath> tryApply(String s) {
        return Operators.exceptionToNone(() -> apply(s));
    }

    @Override
    public String toString() {
        return String.format("%s/%s", namespace, name);
    }

    public static class Serializer extends StdSerializer<ResourcePath> {

        private Serializer() {
            super(ResourcePath.class);
        }

        @Override
        public void serialize(ResourcePath value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.toString());
        }

    }

    public static class Deserializer extends StdDeserializer<ResourcePath> {

        private Deserializer() {
            super(ResourceName.class);
        }

        @Override
        public ResourcePath deserialize(JsonParser p, DeserializationContext ignore) throws IOException {
            return ResourcePath.apply(p.readValueAs(String.class));
        }

    }

}
