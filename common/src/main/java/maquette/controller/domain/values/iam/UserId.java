package maquette.controller.domain.values.iam;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.thedeanda.lorem.Lorem;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonSerialize(using = UserId.Serializer.class)
@JsonDeserialize(using = UserId.Deserializer.class)
public class UserId {

    private final String id;

    public static UserId apply(String id) {
        return new UserId(id);
    }

    public static UserId random() {
        return apply(Lorem.getFirstName().toLowerCase());
    }

    public static UserId apply(User executor, String id) {
        if (Objects.isNull(id)) {
            return executor.getUserId();
        } else {
            return UserId.apply(id);
        }
    }

    public String toString() {
        return id;
    }

    public static class Serializer extends StdSerializer<UserId> {

        private Serializer() {
            super(UserId.class);
        }

        @Override
        public void serialize(UserId value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.getId());
        }

    }

    public static class Deserializer extends StdDeserializer<UserId> {

        private Deserializer() {
            super(UserId.class);
        }

        @Override
        public UserId deserialize(JsonParser p, DeserializationContext ignore) throws IOException {
            return UserId.apply(p.readValueAs(String.class));
        }

    }

}
