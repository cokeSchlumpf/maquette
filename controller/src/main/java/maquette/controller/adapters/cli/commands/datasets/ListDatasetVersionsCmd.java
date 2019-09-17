package maquette.controller.adapters.cli.commands.datasets;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.DataTable;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.adapters.cli.commands.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.VersionInfo;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListDatasetVersionsCmd implements Command {

    private final String namespace;

    private final String dataset;

    @JsonCreator
    public static ListDatasetVersionsCmd apply(
        @JsonProperty("namespace") String namespace,
        @JsonProperty("dataset") String dataset) {
        return new ListDatasetVersionsCmd(namespace, dataset);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(dataset, "dataset");
        ResourcePath datasetResource = ResourcePath.apply(executor, namespace, dataset);


        return app
            .datasets()
            .getDetails(executor, datasetResource)
            .thenApply(details -> {
                Comparator<VersionInfo> comparing = Comparator
                    .<VersionInfo, VersionTag>comparing(v -> v.getVersion().orElse(VersionTag.apply(String.valueOf(Integer.MAX_VALUE))))
                    .reversed();

                List<VersionInfo> sorted = details
                    .getVersions()
                    .stream()
                    .sorted(comparing)
                    .collect(Collectors.toList());


                DataTable dt = DataTable.apply("version", "records", "modified", "by", "id");

                for (VersionInfo v : sorted) {
                    dt = dt.withRow(
                        v.getVersion(),
                        v.getRecords(),
                        v.getLastModified(),
                        v.getModifiedBy(),
                        v.getVersionId());
                }

                return CommandResult.success(dt);
            });
    }

}
