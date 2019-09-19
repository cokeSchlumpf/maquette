package maquette.sdk.databind;

import org.apache.avro.Schema;

import maquette.sdk.util.Records;

public interface AvroDeserializer<T> {

    Class<T> getRecordType();

    Iterable<T> mapRecords(Records records);

}
