package maquette.controller.adapters.cli.commands.projects;

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
import maquette.controller.adapters.cli.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespaceGrant;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PrintProjectDetailsCmd implements Command {

    private static final String PROJECT = "project";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonCreator
    public static PrintProjectDetailsCmd apply(@JsonProperty(PROJECT) ResourceName namespace) {
        return new PrintProjectDetailsCmd(namespace);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(project, PROJECT);

        return app
            .projects()
            .getDetails(executor, project)
            .thenApply(details -> {
                StringWriter sw = new StringWriter();
                PrintWriter out = new PrintWriter(sw);

                DataTable properties = DataTable
                    .apply("key", "property")
                    .withRow("owner", details.getDetails().getAcl().getOwner().getAuthorization())
                    .withRow("private", details.getProperties().isPrivate())
                    .withRow("", "")
                    .withRow("created", details.getDetails().getCreated())
                    .withRow("created by", details.getDetails().getCreatedBy())
                    .withRow("", "")
                    .withRow("modified", details.getDetails().getModified())
                    .withRow("modified by", details.getDetails().getModifiedBy())
                    .withRow("", "")
                    .withRow("datasets", details.getDetails().getDatasets().size());

                DataTable acl = DataTable.apply("granted to", "privilege", "granted by", "granted at");

                for (NamespaceGrant grant : details.getDetails().getAcl().getGrants()) {
                    acl = acl.withRow(
                        grant.getAuthorization().getAuthorization(),
                        grant.getPrivilege().name,
                        grant.getAuthorization().getBy(),
                        grant.getAuthorization().getAt());
                }

                // TODO format markdown for ASCII
                out.println(details.getProperties().getDescription());
                out.println();

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
