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

@AllArgsConstructor(staticName = "apply")
public class InMemoryDataStorageAdapter implements DataStorageAdapter {

    private final Map<UID, List<GenericData.Record>> data;

    public static InMemoryDataStorageAdapter apply() {
        return apply(Maps.newHashMap());
    }

    @Override
    public void append(UID versionId, List<GenericData.Record> data) {
        if (this.data.containsKey(versionId)) {
            this.data.get(versionId).addAll(data);
        } else {
            this.data.put(versionId, Lists.newArrayList(data));
        }
    }

    @Override
    public void clean(UID versionId) {
        this.data.remove(versionId);
    }

    @Override
    public List<GenericData.Record> get(UID versionId) {
        if (this.data.containsKey(versionId)) {
            return ImmutableList.copyOf(this.data.get(versionId));
        } else {
            return Lists.newArrayList();
        }
    }

}
