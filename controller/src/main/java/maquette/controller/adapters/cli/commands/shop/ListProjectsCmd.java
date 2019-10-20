package maquette.controller.adapters.cli.commands.shop;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.DataTable;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.deprecatedproject.ProjectDetails;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListProjectsCmd implements Command {

    @JsonCreator
    public static ListProjectsCmd apply() {
        return new ListProjectsCmd();
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        return app
            .shop()
            .listProjects(executor)
            .thenApply(project -> Operators.suppressExceptions(() -> {
                DataTable dt = DataTable.apply("name", "owner", "private", "modified", "datasets");

                List<ProjectDetails> sorted = project
                    .stream()
                    .sorted(Comparator.comparing(p -> p.getProperties().getName().getValue()))
                    .collect(Collectors.toList());

                for (ProjectDetails info : sorted) {
                    dt = dt.withRow(
                        info.getProperties().getName(),
                        info.getDetails().getAcl().getOwner().getAuthorization(),
                        info.getProperties().isPrivate(),
                        info.getDetails().getModified(),
                        info.getDetails().getDatasets().size());
                }

                return CommandResult.success(dt.toAscii(), dt);
            }));
    }

}
