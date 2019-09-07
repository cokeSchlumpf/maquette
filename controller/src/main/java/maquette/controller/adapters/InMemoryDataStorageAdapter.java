package maquette.controller.adapters;

import java.util.List;
import java.util.Map;

import org.apache.avro.generic.GenericData;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.AllArgsConstructor;
import maquette.controller.domain.ports.DataStorageAdapter;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.records.Records;

@AllArgsConstructor(staticName = "apply")
public class InMemoryDataStorageAdapter implements DataStorageAdapter {

    private final Map<UID, List<GenericData.Record>> data;

    public static InMemoryDataStorageAdapter apply() {
        return apply(Maps.newHashMap());
    }

    @Override
    public void append(UID versionId, Records data) {
        if (this.data.containsKey(versionId)) {
            this.data.get(versionId).addAll(data.getRecords());
        } else {
            this.data.put(versionId, Lists.newArrayList(data.getRecords()));
        }
    }

    @Override
    public void clean(UID versionId) {
        this.data.remove(versionId);
    }

    @Override
    public Records get(UID versionId) {
        if (this.data.containsKey(versionId)) {
            return Records.fromRecords(this.data.get(versionId));
        } else {
            return Records.empty();
        }
    }

}
