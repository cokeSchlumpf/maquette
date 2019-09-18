package maquette.sdk.databind;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.avro.AvroMapper;
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
        initializeMapper(om);

        if (pretty) {
            om.enable(SerializationFeature.INDENT_OUTPUT);
        }

        return om;
    }

    public ObjectMapper create() {
        return create(false);
    }

    public AvroMapper createAvroMapper() {
        AvroMapper om = new AvroMapper();
        initializeMapper(om);
        return om;
    }

    private void initializeMapper(ObjectMapper om) {
        om.registerModule(new GuavaModule());
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());

        om.getSerializationConfig().getDefaultVisibilityChecker()
          .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
          .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
          .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
          .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
          .withCreatorVisibility(JsonAutoDetect.Visibility.ANY);

        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
}
