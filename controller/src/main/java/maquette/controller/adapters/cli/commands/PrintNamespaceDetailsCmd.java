package maquette.controller.adapters.cli.commands;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.DataTable;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PrintNamespaceDetailsCmd implements Command {

    private final String namespace;

    @JsonCreator
    public static PrintNamespaceDetailsCmd apply(@JsonProperty("namespace") String namespace) {
        return new PrintNamespaceDetailsCmd(namespace);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        ResourceName resource;

        if (namespace == null) {
            resource = ResourceName.apply(executor.getUserId().getId());
        } else {
            resource = ResourceName.apply(namespace);
        }

        return app
            .namespaces()
            .getNamespaceDetails(executor, resource)
            .thenApply(details -> {
                DataTable dt = DataTable
                    .apply("key", "property")
                    .withRow("owner", details.getAcl().getOwner().getAuthorization().toString())
                    .withRow("", "")
                    .withRow("created", sdf.format(Date.from(details.getCreated())))
                    .withRow("created by", details.getCreatedBy().getId())
                    .withRow("", "")
                    .withRow("modified", sdf.format(Date.from(details.getModified())))
                    .withRow("modified by", details.getModifiedBy().getId());

                return CommandResult.success(dt.toAscii(false, true));
            });
    }

}
