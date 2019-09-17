package maquette.controller.adapters.cli.commands.datasets;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.DataTable;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.adapters.cli.commands.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.Commit;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PrintDatasetVersionDetailsCmd implements Command {

    private final String namespace;

    private final String dataset;

    private final String version;

    @JsonCreator
    public static PrintDatasetVersionDetailsCmd apply(
        @JsonProperty("namespace") String namespace,
        @JsonProperty("dataset") String dataset,
        @JsonProperty("version") String version) {
        return new PrintDatasetVersionDetailsCmd(namespace, dataset, version);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(dataset, "dataset");
        ResourcePath datasetResource = ResourcePath.apply(executor, namespace, dataset);


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
