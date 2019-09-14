package maquette.controller.adapters.cli.commands;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.DataTable;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespaceInfo;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListNamespacesCmd implements Command {

    @JsonCreator
    public static ListNamespacesCmd apply() {
        return new ListNamespacesCmd();
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        return app
            .namespaces()
            .listNamespaces(executor)
            .thenApply(namespaces -> Operators.suppressExceptions(() -> {
                DataTable dt = DataTable.apply("name", "owner", "modified", "datasets");

                List<NamespaceInfo> sorted = namespaces
                    .stream()
                    .sorted(Comparator.comparing(ns -> ns.getName().getValue()))
                    .collect(Collectors.toList());

                for (NamespaceInfo info : sorted) {
                    dt = dt.withRow(
                        info.getName().getValue(),
                        info.getAcl().getOwner().getAuthorization().toString(),
                        sdf.format(Date.from(info.getModified())),
                        String.valueOf(info.getDatasets().size()));
                }

                return CommandResult.success(dt.toAscii(), dt);
            }));
    }

}
