package maquette.controller.application.commands.commands.datasets;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.application.commands.CommandResult;
import maquette.controller.application.commands.DataTable;
import maquette.controller.application.commands.commands.Command;
import maquette.controller.application.commands.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.Commit;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PrintDatasetVersionDetailsCmd implements Command {

    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String VERSION = "version";

    @JsonProperty(PROJECT)
    private final String project;

    @JsonProperty(DATASET)
    private final String dataset;

    @JsonProperty(VERSION)
    private final String version;

    @JsonCreator
    public static PrintDatasetVersionDetailsCmd apply(
        @JsonProperty(PROJECT) String project,
        @JsonProperty(DATASET) String dataset,
        @JsonProperty(VERSION) String version) {
        
        return new PrintDatasetVersionDetailsCmd(project, dataset, version);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(dataset, DATASET);
        ResourcePath datasetResource = ResourcePath.apply(executor, project, dataset);


        CompletionStage<VersionDetails> versionDetails;

        if (Objects.isNull(version)) {
            versionDetails = app
                .datasets()
                .getVersionDetails(executor, datasetResource);
        } else {
            versionDetails = app
                .datasets()
                .getVersionDetails(executor, datasetResource, VersionTag.apply(version));
        }

        return versionDetails
            .thenApply(details -> {
                StringWriter sw = new StringWriter();
                PrintWriter out = new PrintWriter(sw);

                DataTable properties = DataTable
                    .apply("key", "value")
                    .withRow("id", details.getVersionId())
                    .withRow("records", details.getRecords());

                if (details.getCommit().isPresent()) {
                    Commit commit = details.getCommit().get();
                    properties = properties
                        .withRow("", "")
                        .withRow("short description", commit.getMessage())
                        .withRow("committed", commit.getCommittedAt())
                        .withRow("committed by", commit.getCommittedBy());
                }

                properties = properties
                    .withRow("", "")
                    .withRow("created", details.getCreated())
                    .withRow("created by", details.getCreatedBy())
                    .withRow("", "")
                    .withRow("modified", details.getLastModified())
                    .withRow("modified by", details.getModifiedBy());

                out.println("PROPERTIES");
                out.println("----------");
                out.println(properties.toAscii(false, true));
                out.println();
                out.println("SCHEMA");
                out.println("------");
                out.println(details.getSchema().toString(true));

                return CommandResult.success(sw.toString(), properties);
            });
    }

}
