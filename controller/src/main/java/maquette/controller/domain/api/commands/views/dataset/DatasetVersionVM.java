package maquette.controller.domain.api.commands.views.dataset;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.avro.Schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.CommandResult;
import maquette.controller.domain.api.commands.DataTable;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.ViewModel;
import maquette.controller.domain.values.dataset.Commit;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.dataset.VersionTagInfo;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasetVersionVM implements ViewModel {

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
    public static DatasetVersionVM apply(
        @JsonProperty(VERSION) String version,
        @JsonProperty(MESSAGE) String message,
        @JsonProperty(RECORDS) long records,
        @JsonProperty(COMMITTED_BY) String committedBy,
        @JsonProperty(COMMITTED) String committed,
        @JsonProperty(SCHEMA) Schema schema) {

        return new DatasetVersionVM(
            version, message, records, committedBy, committed, schema);
    }

    public static DatasetVersionVM apply(VersionTagInfo tag, OutputFormat of) {
        return apply(
            of.format(tag.getVersion()),
            of.format(tag.getCommit().getMessage()),
            tag.getRecords(),
            of.format(tag.getCommit().getCommittedBy()),
            of.format(tag.getCommit().getCommittedAt()),
            tag.getSchema());
    }

    public static DatasetVersionVM apply(VersionDetails details, OutputFormat of) {
        return apply(
            of.format(details.getVersionId()),
            of.format(details.getCommit().map(Commit::getMessage)),
            details.getRecords(),
            of.format(details.getCommit().map(Commit::getCommittedBy)),
            of.format(details.getCommit().map(Commit::getCommittedAt)),
            details.getSchema());
    }

    @Override
    public CommandResult toCommandResult(ObjectMapper om) {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);

        DataTable properties = DataTable
            .apply("key", "value")
            .withRow("id", version)
            .withRow("records", records)
            .withRow("", "")
            .withRow("short description", message)
            .withRow("committed", committed)
            .withRow("committed by", committedBy);

        out.println("PROPERTIES");
        out.println("----------");
        out.println(properties.toAscii(false, true));
        out.println();
        out.println("SCHEMA");
        out.println("------");
        out.println(schema.toString(true));

        return CommandResult.success(sw.toString(), properties);
    }
}
