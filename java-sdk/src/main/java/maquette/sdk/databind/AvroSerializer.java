package maquette.sdk.databind;

import org.apache.avro.Schema;

import maquette.controller.domain.values.core.records.Records;

public interface AvroSerializer<T> {

    Class<T> getModel();

    Schema getSchema();

    Records mapRecords(Iterable<T> value);

}
