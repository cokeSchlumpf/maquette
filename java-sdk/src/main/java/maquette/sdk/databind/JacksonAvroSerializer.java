package maquette.sdk.databind;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;

import com.fasterxml.jackson.dataformat.avro.AvroMapper;
import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import maquette.sdk.util.Operators;
import maquette.sdk.util.Records;

@AllArgsConstructor(staticName = "apply")
public final class JacksonAvroSerializer<T> implements AvroSerializer<T> {

    private final AvroMapper mapper;

    private final Class<T> type;

    @Override
    public Class<T> getRecordType() {
        return type;
    }

    @Override
    public Schema getSchema() {
        return Operators.suppressExceptions(() -> mapper.schemaFor(type).getAvroSchema());
    }

    @Override
    public Records mapRecords(Iterable<T> values) {
        return Operators.suppressExceptions(() -> {
            List<GenericData.Record> records = Lists.newArrayList();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            mapper.writer(mapper.schemaFor(type)).writeValues(os).writeAll(values);

            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(new ByteArrayInputStream(os.toByteArray()), null);
            DatumReader<GenericData.Record> datumReader = new GenericDatumReader<>(getSchema());

            while (!decoder.isEnd()) {
                records.add(datumReader.read(null, decoder));
            }

            return Records.apply(getSchema(), records);
        });
    }

}
