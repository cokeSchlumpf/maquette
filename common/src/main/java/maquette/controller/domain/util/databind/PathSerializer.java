package maquette.controller.domain.util.databind;

import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public final class PathSerializer extends StdSerializer<Path> {

    public PathSerializer() {
        super(Path.class);
    }

    @Override
    public void serialize(Path value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(value.toString());
    }

}

