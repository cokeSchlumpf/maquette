package maquette.sdk.databind;

import maquette.controller.domain.values.core.records.Records;

public interface AvroDeserializer<T> {

    Class<T> getRecordType();

    Iterable<T> mapRecords(Records records);

}
