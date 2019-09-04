package maquette.controller.domain.ports;

import java.util.List;

import org.apache.avro.generic.GenericData;

import maquette.controller.domain.values.core.UID;

public interface DataStorageAdapter {

    void append(UID versionId, List<GenericData.Record> data);

    void clean(UID versionId);

    List<GenericData.Record> get(UID versionId);

}
