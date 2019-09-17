package maquette.controller.adapters.cli.commands.datasets;

import java.io.PrintWriter;
import java.io.StringWriter;
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
import maquette.controller.domain.values.dataset.DatasetGrant;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PrintDatasetDetailsCmd implements Command {

    private final String namespace;

    private final String dataset;

    @JsonCreator
    public static PrintDatasetDetailsCmd apply(
        @JsonProperty("namespace") String namespace,
        @JsonProperty("dataset") String dataset) {
        return new PrintDatasetDetailsCmd(namespace, dataset);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(dataset, "dataset");
        ResourcePath datasetResource = ResourcePath.apply(executor, namespace, dataset);


        return app
            .datasets()
            .getDetails(executor, datasetResource)
            .thenApply(details -> {
                StringWriter sw = new StringWriter();
                PrintWriter out = new PrintWriter(sw);

                DataTable properties = DataTable
                    .apply("key", "value")
                    .withRow("owner", details.getAcl().getOwner().getAuthorization())
                    .withRow("", "")
                    .withRow("created", details.getCreated())
                    .withRow("created by", details.getCreatedBy())
                    .withRow("", "")
                    .withRow("modified", details.getModified())
                    .withRow("modified by", details.getModifiedBy())
                    .withRow("", "")
                    .withRow("versions", details.getVersions().size());

                DataTable acl = DataTable.apply("granted to", "privilege", "granted by", "granted at");

                for (DatasetGrant grant : details.getAcl().getGrants()) {
                    acl = acl.withRow(
                        grant.getAuthorization().getAuthorization(),
                        grant.getPrivilege().name,
                        grant.getAuthorization().getBy(),
                        grant.getAuthorization().getAt());
                }

                out.println("PROPERTIES");
                out.println("----------");
                out.println(properties.toAscii(false, true));
                out.println();
                out.println("ACCESS CONTROL");
                out.println("--------------");
                out.println(acl.toAscii());

                return CommandResult.success(sw.toString(), properties, acl);
            });
    }

}
