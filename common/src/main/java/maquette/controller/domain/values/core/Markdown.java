package maquette.controller.domain.values.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
@JsonSerialize(using = Markdown.Serializer.class)
@JsonDeserialize(using = Markdown.Deserializer.class)
public class Markdown {

    private final String value;

    public static Markdown apply() {
        return apply("");
    }

    public static class Serializer extends StdSerializer<Markdown> {

        private Serializer() {
            super(Markdown.class);
        }

        @Override
        public void serialize(Markdown value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.getValue());
        }

    }

    public static class Deserializer extends StdDeserializer<Markdown> {

        private Deserializer() {
            super(ResourceName.class);
        }

        @Override
        public Markdown deserialize(JsonParser p, DeserializationContext ignore) throws IOException {
            return Markdown.apply(p.readValueAs(String.class));
        }

    }

}
