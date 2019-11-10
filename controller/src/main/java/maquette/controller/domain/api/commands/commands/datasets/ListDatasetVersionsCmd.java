package maquette.controller.domain.api.commands.commands.datasets;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.domain.api.commands.CommandResult;
import maquette.controller.domain.api.commands.DataTable;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.commands.Command;
import maquette.controller.domain.api.commands.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.api.commands.views.DatasetVersionsVM;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.VersionInfo;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.dataset.VersionTagInfo;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListDatasetVersionsCmd implements Command {

    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(DATASET)
    private final ResourceName dataset;

    @JsonCreator
    public static ListDatasetVersionsCmd apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(DATASET) ResourceName dataset) {

        return new ListDatasetVersionsCmd(project, dataset);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app,
                                              OutputFormat outputFormat) {
        ObjectValidation.notNull().validate(dataset, DATASET);
        ResourcePath datasetResource = ResourcePath.apply(executor, project, dataset);


        return app
            .datasets()
            .getDetails(executor, datasetResource)
            .thenApply(details -> {
                Comparator<VersionTagInfo> comparing = Comparator
                    .comparing(VersionTagInfo::getVersion)
                    .reversed();

                List<VersionTagInfo> sorted = details
                    .getVersions()
                    .stream()
                    .sorted(comparing)
                    .collect(Collectors.toList());


                DataTable dt = DataTable.apply("version", "records", "committed by", "committed at", "message");

                for (VersionTagInfo v : sorted) {
                    dt = dt.withRow(
                        v.getVersion(),
                        v.getRecords(),
                        v.getCommit().getCommittedBy(),
                        v.getCommit().getCommittedAt(),
                        v.getCommit().getMessage());
                }

                return CommandResult
                    .success(dt.toAscii(), dt)
                    .withView(DatasetVersionsVM.apply(sorted, outputFormat));
            });
    }

}
