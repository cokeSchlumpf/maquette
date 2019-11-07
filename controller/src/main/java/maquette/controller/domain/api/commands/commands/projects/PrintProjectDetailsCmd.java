package maquette.controller.domain.api.commands.commands.projects;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.domain.api.commands.CommandResult;
import maquette.controller.domain.api.commands.DataTable;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.commands.Command;
import maquette.controller.domain.api.commands.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.api.commands.views.AuthorizationVM;
import maquette.controller.domain.api.commands.views.MembersEntryVM;
import maquette.controller.domain.api.commands.views.ProjectVM;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectGrant;

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
    public CompletionStage<CommandResult> run(User executor, CoreApplication app,
                                              OutputFormat outputFormat) {
        ObjectValidation.notNull().validate(project, PROJECT);

        return app
            .projects()
            .getDetails(executor, project)
            .thenApply(details -> {
                StringWriter sw = new StringWriter();
                PrintWriter out = new PrintWriter(sw);

                final DataTable properties = DataTable
                    .apply("key", "property")
                    .withRow("owner", details.getAcl().getOwner().getAuthorization())
                    .withRow("private", details.getAcl().isPrivate())
                    .withRow("", "")
                    .withRow("created", details.getCreated())
                    .withRow("created by", details.getCreatedBy())
                    .withRow("", "")
                    .withRow("modified", details.getModified())
                    .withRow("modified by", details.getModifiedBy())
                    .withRow("", "")
                    .withRow("datasets", details.getDatasets().size());

                DataTable acl = DataTable.apply("granted to", "privilege", "granted by", "granted at");
                List<MembersEntryVM> aclVM = Lists.newArrayList();

                for (ProjectGrant grant : details.getAcl().getGrants()) {
                    acl = acl.withRow(
                        grant.getAuthorization().getAuthorization(),
                        grant.getPrivilege().name,
                        grant.getAuthorization().getBy(),
                        grant.getAuthorization().getAt());

                    aclVM.add(MembersEntryVM.apply(
                        outputFormat.format(grant.getAuthorization().getAuthorization()),
                        outputFormat.format(grant.getPrivilege().name),
                        outputFormat.format(grant.getAuthorization().getBy()),
                        outputFormat.format(grant.getAuthorization().getAt())));
                }

                out.println(details.getDescription().asASCIIString());
                out.println();

                out.println("PROPERTIES");
                out.println("----------");
                out.println(properties.toAscii(false, true));
                out.println();
                out.println("ACCESS CONTROL");
                out.println("--------------");
                out.println(acl.toAscii());

                ProjectVM vm = ProjectVM.apply(
                    outputFormat.format(details.getName()),
                    details.getDescription().asHTMLString(),
                    details.getAcl().isPrivate(),
                    AuthorizationVM.apply(details.getAcl().getOwner()),
                    aclVM);

                return CommandResult.success(sw.toString(), properties, acl).withView(vm);
            });
    }

}
