package maquette.sdk.databind;

import org.apache.avro.Schema;

import maquette.sdk.util.Records;

public interface AvroSerializer<T> {

    Class<T> getRecordType();

    Schema getSchema();

    Records mapRecords(Iterable<T> value);

}
