package maquette.sdk.databind;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.avro.AvroMapper;

import lombok.AllArgsConstructor;
import maquette.sdk.util.Operators;
import maquette.sdk.util.Records;

@AllArgsConstructor(staticName = "apply")
public final class JacksonAvroDeserializer<T> implements AvroDeserializer<T> {

    private final AvroMapper mapper;

    private final Class<T> recordType;

    @Override
    public Class<T> getRecordType() {
        return recordType;
    }

    @Override
    public Iterable<T> mapRecords(Records records) {
        ByteArrayInputStream is = createSchemalessBinary(records);

        MappingIterator<T> objectMappingIterator = Operators.suppressExceptions(() -> mapper
            .readerFor(recordType)
            .with(mapper.schemaFor(recordType))
            .readValues(is));

        return Operators.suppressExceptions((Operators.ExceptionalSupplier<List<T>>) objectMappingIterator::readAll);
    }

    private ByteArrayInputStream createSchemalessBinary(Records records) {
        return Operators.suppressExceptions(() -> {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(records.getSchema());
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(os, null);

            for (GenericData.Record record : records.getRecords()) {
                datumWriter.write(record, encoder);
            }

            encoder.flush();

            return new ByteArrayInputStream(os.toByteArray());
        });
    }
}
