package maquette.controller.domain.entities.dataset.protocol.events;

import org.apache.avro.Schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.VersionEvent;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.dataset.Commit;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommittedDatasetVersion implements VersionEvent {

    private static final String COMMIT = "commit";
    private static final String DATASET = "dataset";
    private static final String SCHEMA = "schema";
    private static final String RECORDS = "records";
    private static final String VERSION_ID = "version-id";

    @JsonProperty(VERSION_ID)
    private final UID versionId;

    @JsonProperty(COMMIT)
    private final Commit commit;

    @JsonProperty(SCHEMA)
    private final Schema schema;

    @JsonProperty(RECORDS)
    private final long records;

    @JsonCreator
    public static CommittedDatasetVersion apply(
        @JsonProperty(VERSION_ID) UID id,
        @JsonProperty(COMMIT) Commit commit,
        @JsonProperty(SCHEMA) Schema schema,
        @JsonProperty(RECORDS) long records) {

        return new CommittedDatasetVersion(id, commit, schema, records);
    }

}
