package maquette.controller.domain.api.commands.views;

import org.apache.avro.Schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.values.dataset.VersionTagInfo;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasetVersionCardVM {

    private static final String COMMITTED = "committed";
    private static final String COMMITTED_BY = "committed-by";
    private static final String MESSAGE = "message";
    private static final String RECORDS = "records";
    private static final String SCHEMA = "schema";
    private static final String VERSION = "version";

    @JsonProperty(VERSION)
    private final String version;

    @JsonProperty(MESSAGE)
    private final String message;

    @JsonProperty(RECORDS)
    private final long records;

    @JsonProperty(COMMITTED_BY)
    private final String committedBy;

    @JsonProperty(COMMITTED)
    private final String committed;

    @JsonProperty(SCHEMA)
    private final Schema schema;

    @JsonCreator
    public static DatasetVersionCardVM apply(
        @JsonProperty(VERSION) String version,
        @JsonProperty(MESSAGE) String message,
        @JsonProperty(RECORDS) long records,
        @JsonProperty(COMMITTED_BY) String committedBy,
        @JsonProperty(COMMITTED) String committed,
        @JsonProperty(SCHEMA) Schema schema) {

        return new DatasetVersionCardVM(
            version, message, records, committedBy, committed, schema);
    }

    public static DatasetVersionCardVM apply(VersionTagInfo tag, OutputFormat of) {
        return apply(
            of.format(tag.getVersion()),
            of.format(tag.getCommit().getMessage()),
            tag.getRecords(),
            of.format(tag.getCommit().getCommittedBy()),
            of.format(tag.getCommit().getCommittedAt()),
            tag.getSchema());
    }

}
