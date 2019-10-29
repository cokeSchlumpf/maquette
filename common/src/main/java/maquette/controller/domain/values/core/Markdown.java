package maquette.controller.domain.values.core;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

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

    public static Markdown fromResource(String resource, Map<String, Object> values) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate(String.format("notifications/%s", resource));
        JtwigModel model = JtwigModel.newModel(values);

        return apply(template.render(model));
    }

    public String asASCIIString() {
        // TODO: Implement transformation
        return value;
    }

    public String asHTMLString() {
        // TODO: Implement
        return value;
    }

    public String asPlainText() {
        // TODO: Implement
        return value;
    }

    public String asPreviewText() {
        final int maxWords = 25;
        final int maxRange = 35;
        final String[] words = asPlainText().split("\\s+");
        final int wordCount = asPlainText().split("\\s+").length;

        if (wordCount <= maxWords + maxRange) {
            return asPlainText();
        } else {
            final String[] firstWords =  ArrayUtils.subarray(words, 0, maxWords);
            return String.join(" ", firstWords) + " ...";
        }
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
