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

    private final ResourceName project;

    private final ResourceName name;

    @JsonCreator
    public static ResourcePath apply(
        @JsonProperty("project") ResourceName project,
        @JsonProperty("name") ResourceName name) {

        return new ResourcePath(project, name);
    }

    public static ResourcePath apply(
        String project,
        String name) {
        return new ResourcePath(
            ResourceName.apply(project),
            ResourceName.apply(name));
    }

    public static ResourcePath apply(
        User executor,
        String project,
        String name) {

        return apply(Objects.isNull(project) || project.equals("_") ? executor.getUserId().getId() : project, name);
    }

    public static ResourcePath apply(
        User executor,
        ResourceName project,
        ResourceName name) {

        return apply(Objects.isNull(project) || project.getValue().equals("_") ? executor.getUserId().getId() : project.getValue(), name.getValue());
    }

    public static ResourcePath apply(String s) {
        try {
            String[] parts = s.split("/");

            if (parts.length == 2) {
                ResourceName project = ResourceName.apply(parts[0]);
                ResourceName name = ResourceName.apply(parts[1]);

                return apply(project, name);
            } else {
                throw InvalidResourceNameException.apply(s);
            }
        } catch (Exception e) {
            throw InvalidResourceNameException.apply(s);
        }
    }

    @Override
    public String toString() {
        return String.format("%s/%s", project, name);
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
