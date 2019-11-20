package maquette.controller.domain.api.commands.commands.datasets;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CompletionStage;

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
import maquette.controller.domain.api.commands.views.DatasetVM;
import maquette.controller.domain.values.core.Executed;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.governance.AccessRequest;
import maquette.controller.domain.values.dataset.DatasetGrant;
import maquette.controller.domain.values.dataset.DatasetMember;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PrintDatasetDetailsCmd implements Command {

    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(DATASET)
    private final ResourceName dataset;

    @JsonCreator
    public static PrintDatasetDetailsCmd apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(DATASET) ResourceName dataset) {
        return new PrintDatasetDetailsCmd(project, dataset);
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
                StringWriter sw = new StringWriter();
                PrintWriter out = new PrintWriter(sw);

                DataTable properties = DataTable
                    .apply("key", "value")
                    .withRow("owner", details.getAcl().getOwner().getAuthorization())
                    .withRow("requires approval", details.getGovernance().isApprovalRequired())
                    .withRow("classification", details.getGovernance().getClassification())
                    .withRow("", "")
                    .withRow("created", details.getCreated())
                    .withRow("created by", details.getCreatedBy())
                    .withRow("", "")
                    .withRow("modified", details.getModified())
                    .withRow("modified by", details.getModifiedBy())
                    .withRow("", "")
                    .withRow("versions", details.getVersions().size());

                DataTable acl = DataTable.apply("granted to", "privilege", "granted by", "granted at");

                for (DatasetMember grant : details.getAcl().getMembers()) {
                    acl = acl.withRow(
                        grant.getAuthorization().getAuthorization(),
                        grant.getPrivilege().name,
                        grant.getAuthorization().getBy(),
                        grant.getAuthorization().getAt());
                }

                details.getDescription().ifPresent(description -> {
                    out.println(description.getValue());
                    out.println();
                });

                DataTable grants = DataTable.apply("request for", "privilege", "requested", "id");

                for (DatasetGrant grant : details.getAcl().getOpenGrants()) {
                    grants = grants.withRow(
                        grant.getGrantFor(),
                        grant.getGrant(),
                        grant.getRequest().map(AccessRequest::getExecuted).map(Executed::getAt),
                        grant.getId());
                }

                out.println("PROPERTIES");
                out.println("----------");
                out.println(properties.toAscii(false, true));
                out.println();
                out.println("MEMBERS");
                out.println("-------");
                out.println(acl.toAscii());
                out.println();
                out.println("OPEN ACCESS REQUESTS");
                out.println("--------------------");
                out.println(grants.toAscii());

                return CommandResult
                    .success(sw.toString(), properties, acl, grants)
                    .withView(DatasetVM.apply(details, executor, outputFormat));
            });
    }

}
