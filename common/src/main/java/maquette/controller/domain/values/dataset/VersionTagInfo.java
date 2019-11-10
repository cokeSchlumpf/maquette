package maquette.controller.domain.values.dataset;

import org.apache.avro.Schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.UID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VersionTagInfo {

    private static final String COMMIT = "commit";
    private static final String ID = "id";
    private static final String RECORDS = "records";
    private static final String SCHEMA = "schema";
    private static final String VERSION = "version";

    @JsonProperty(ID)
    private final UID id;

    @JsonProperty(VERSION)
    private final VersionTag version;

    @JsonProperty(SCHEMA)
    private final Schema schema;

    @JsonProperty(RECORDS)
    private final long records;

    @JsonProperty(COMMIT)
    private final Commit commit;

    @JsonCreator
    public static VersionTagInfo apply(
        @JsonProperty(ID) UID id,
        @JsonProperty(VERSION) VersionTag version,
        @JsonProperty(SCHEMA) Schema schema,
        @JsonProperty(RECORDS) long records,
        @JsonProperty(COMMIT) Commit commit) {

        return new VersionTagInfo(id, version, schema, records, commit);
    }

    public VersionTag nextVersion(Schema newSchema) {
        boolean schemaNotCompatible = newSchema
            .getFields()
            .stream()
            .anyMatch(field -> {
                Schema.Field existingField = schema.getField(field.name());
                return !field.equals(existingField);
            });

        if (schemaNotCompatible) {
            return VersionTag.apply(version.getMajor() + 1, 0, 0);
        } else {
            return VersionTag.apply(version.getMajor(), version.getMinor() + 1, 0);
        }
    }

}
