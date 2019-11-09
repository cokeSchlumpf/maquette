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

    private final UID id;

    private final VersionTag version;

    private final Schema schema;

    private final Commit commit;

    @JsonCreator
    public static VersionTagInfo apply(
        @JsonProperty("id") UID id,
        @JsonProperty("version") VersionTag version,
        @JsonProperty("schema") Schema schema,
        @JsonProperty("commit") Commit commit) {

        return new VersionTagInfo(id, version, schema, commit);
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
