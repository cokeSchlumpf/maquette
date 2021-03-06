package maquette.controller.domain.util.databind;

import java.nio.file.Path;

import org.apache.avro.Schema;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class ObjectMapperFactory {

    private ObjectMapperFactory() {

    }

    public static ObjectMapperFactory apply() {
        return new ObjectMapperFactory();
    }

    public ObjectMapper create(boolean pretty) {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new GuavaModule());
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());

        om.getSerializationConfig()
          .getDefaultVisibilityChecker()
          .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
          .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
          .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
          .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
          .withCreatorVisibility(JsonAutoDetect.Visibility.ANY);

        om.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        om.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        om.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        om.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        om.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Schema.class, new SchemaSerializer());
        module.addDeserializer(Schema.class, new SchemaDeserializer());
        module.addSerializer(Path.class, new PathSerializer());
        module.addDeserializer(Path.class, new PathDeserializer());
        om.registerModule(module);

        if (pretty) {
            om.enable(SerializationFeature.INDENT_OUTPUT);
        }

        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        return om;
    }

    public ObjectMapper create() {
        return create(false);
    }

}
