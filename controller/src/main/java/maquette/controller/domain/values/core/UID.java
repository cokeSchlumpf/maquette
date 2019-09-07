package maquette.controller.domain.values.core;

import java.io.IOException;
import java.util.UUID;

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
import maquette.controller.domain.values.exceptions.InvalidUIDException;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonSerialize(using = UID.Serializer.class)
@JsonDeserialize(using = UID.Deserializer.class)
public class UID {

    private final String value;

    public static UID apply(String value) {
        if (value == null || value.length() == 0) {
            throw InvalidUIDException.apply(value);
        }

        return new UID(value);
    }

    public static UID apply() {
        return apply(UUID.randomUUID().toString());
    }

    public static class Serializer extends StdSerializer<UID> {

        private Serializer() {
            super(UID.class);
        }

        @Override
        public void serialize(UID value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.getValue());
        }

    }

    public static class Deserializer extends StdDeserializer<UID> {

        private Deserializer() {
            super(UID.class);
        }

        @Override
        public UID deserialize(JsonParser p, DeserializationContext ignore) throws IOException {
            return UID.apply(p.readValueAs(String.class));
        }

    }
}
