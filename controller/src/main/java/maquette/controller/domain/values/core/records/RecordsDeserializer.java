package maquette.controller.domain.values.core.records;

import java.io.IOException;

import org.apache.avro.util.ByteBufferOutputStream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public final class RecordsDeserializer extends StdDeserializer<Records> {

    private RecordsDeserializer() {
        super(Records.class);
    }

    @Override
    public Records deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ByteBufferOutputStream os = new ByteBufferOutputStream();
        p.readBinaryValue(os);
        return Records.fromByteBuffers(os.getBufferList());
    }

}
