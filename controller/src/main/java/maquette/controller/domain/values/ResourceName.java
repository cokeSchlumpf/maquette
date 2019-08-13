package maquette.controller.domain.values;

import java.io.IOException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import maquette.

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceName {

    private final String value;

    public static ResourceName apply(String value) {
        if (value == null || value.length() == 0) {
            throw InvalidResourceNameException.apply(value);
        }

        String name = NameFactory
            .apply(NameFactory.Defaults.LOWERCASE_HYPHENATE)
            .create(value);

        if (name.length() == 0) {
            throw InvalidResourceNameException.apply(value);
        }

        return new ResourceName(name);
    }

    @Override
    public String toString() {
        return value;
    }

    public static class Serializer extends StdSerializer<ResourceName> {

        private Serializer() {
            super(ResourceName.class);
        }

        @Override
        public void serialize(ResourceName value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.getValue());
        }

    }

    public static class Deserializer extends StdDeserializer<ResourceName> {

        private Deserializer() {
            super(ResourceName.class);
        }

        @Override
        public ResourceName deserialize(JsonParser p, DeserializationContext ignore) throws IOException {
            return ResourceName.apply(p.readValueAs(String.class));
        }

    }

    public static final class InvalidResourceNameException extends IllegalArgumentException implements AdaException {

        private InvalidResourceNameException(String message) {
            super(message);
        }

        public static InvalidResourceNameException apply(String name) {
            String message = String.format(
                "The provided resource name '%s' is not valid and cannot be transformed to a valid resource name.",
                name);

            return new InvalidResourceNameException(message);
        }

    }


}
