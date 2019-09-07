package maquette.controller.domain.ports;

import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.records.Records;

public interface DataStorageAdapter {

    void append(UID versionId, Records data);

    void clean(UID versionId);

    Records get(UID versionId);

}
