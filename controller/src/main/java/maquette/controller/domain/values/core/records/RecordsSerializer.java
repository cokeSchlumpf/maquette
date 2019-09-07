package maquette.controller.domain.values.core.records;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public final class RecordsSerializer extends StdSerializer<Records> {

    private RecordsSerializer() {
        super(Records.class);
    }

    @Override
    public void serialize(Records value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        EncodedAvroRecords records;

        if (value instanceof EncodedAvroRecords) {
            records = (EncodedAvroRecords) value;
        } else {
            records = (EncodedAvroRecords) Records.fromByteStrings(value.getBytes());
        }

        byte[] bytes = IOUtils.toByteArray(records.asInputStream());
        gen.writeBinary(bytes);
    }

}
