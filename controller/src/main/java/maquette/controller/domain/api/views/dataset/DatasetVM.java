package maquette.controller.domain.api.views.dataset;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.CommandResult;
import maquette.controller.domain.api.DataTable;
import maquette.controller.domain.api.OutputFormat;
import maquette.controller.domain.api.ViewModel;
import maquette.controller.domain.api.views.AuthorizationVM;
import maquette.controller.domain.api.views.MembersEntryVM;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetMember;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectDetails;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatasetVM implements ViewModel {

    private static final String CAN_VIEW_MEMBERS = "can-view-members";

    private static final String ACCESS_REQUESTS = "access-requests";
    private static final String DATASET = "dataset";
    private static final String DETAILS = "details";
    private static final String INHERITED_MEMBERS = "inherited-members";
    private static final String MEMBERS = "members";
    private static final String PROJECT = "project";
    private static final String VERSIONS = "versions";

    @JsonProperty(PROJECT)
    private final String project;

    @JsonProperty(DATASET)
    private final String dataset;

    @JsonProperty(DETAILS)
    private final DatasetDetailsVM details;

    @JsonProperty(VERSIONS)
    private final int versions;

    @JsonProperty(MEMBERS)
    private final List<MembersEntryVM> members;

    @JsonProperty(INHERITED_MEMBERS)
    private final List<MembersEntryVM> inheritedMembers;

    @JsonProperty(ACCESS_REQUESTS)
    private final DatasetAccessRequestsVM accessRequests;

    @JsonProperty(CAN_VIEW_MEMBERS)
    private final boolean canViewMembers;

    @JsonCreator
    public static DatasetVM apply(
        @JsonProperty(PROJECT) String project,
        @JsonProperty(DATASET) String dataset,
        @JsonProperty(DETAILS) DatasetDetailsVM details,
        @JsonProperty(VERSIONS) int versions,
        @JsonProperty(MEMBERS) List<MembersEntryVM> members,
        @JsonProperty(INHERITED_MEMBERS) List<MembersEntryVM> inheritedMembers,
        @JsonProperty(ACCESS_REQUESTS) DatasetAccessRequestsVM accessRequests,
        @JsonProperty(CAN_VIEW_MEMBERS) boolean canViewMembers) {

        return new DatasetVM(
            project, dataset, details, versions,
            ImmutableList.copyOf(members), ImmutableList.copyOf(inheritedMembers), accessRequests,
            canViewMembers);
    }

    public static DatasetVM apply(DatasetDetails details, ProjectDetails project, User executor, OutputFormat of) {
        List<MembersEntryVM> members = details
            .getAcl()
            .getMembers()
            .stream()
            .map(grant -> MembersEntryVM.apply(grant, of))
            .collect(Collectors.toList());

        List<MembersEntryVM> inheritedMembers = project
            .getAcl()
            .getGrants()
            .stream()
            .map(grant -> MembersEntryVM.apply(grant, of))
            .collect(Collectors.toList());

        for (DatasetMember grant : details.getAcl().getMembers()) {
            members.add(MembersEntryVM.apply(grant, of));
        }

        DatasetDetailsVM detailsVM = DatasetDetailsVM.apply(
            details.getDescription().map(Markdown::asPlainText).orElse(null),
            AuthorizationVM.apply(details.getAcl().getOwner()),
            of.format(details.getAcl().isPrivate()),
            of.format(details.getGovernance().isApprovalRequired()),
            of.format(details.getGovernance().getClassification().name),
            of.format(details.getCreatedBy()),
            of.format(details.getCreated()),
            of.format(details.getModifiedBy()),
            of.format(details.getModified()));

        DatasetAccessRequestsVM accessRequestsVM = DatasetAccessRequestsVM.apply(executor, details, of);

        boolean canViewMembers = details.getAcl().canConsume(executor) || details.getAcl().canProduce(executor);

        return apply(
            of.format(details.getDataset().getProject()),
            of.format(details.getDataset().getName()),
            detailsVM,
            details.getVersions().size(),
            members,
            inheritedMembers,
            accessRequestsVM,
            canViewMembers);
    }

    @Override
    public CommandResult toCommandResult(ObjectMapper om) {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);

        DataTable properties = DataTable
            .apply("key", "value")
            .withRow("owner", details.getOwner().getAuthorization())
            .withRow("requires approval", details.getRequiresApproval())
            .withRow("classification", details.getClassification())
            .withRow("", "")
            .withRow("created", details.getCreated())
            .withRow("created by", details.getCreatedBy())
            .withRow("", "")
            .withRow("modified", details.getModified())
            .withRow("modified by", details.getModifiedBy())
            .withRow("", "")
            .withRow("versions", versions);

        DataTable acl = DataTable.apply("granted to", "privilege", "granted by", "granted at");

        for (MembersEntryVM grant : members) {
            acl = acl.withRow(
                grant.getGrantedTo(),
                grant.getPrivilege(),
                grant.getGrantedBy(),
                grant.getGrantedAt());
        }

        if (details.getDescription() != null) {
            out.println(details.getDescription());
            out.println();
        }

        DataTable grants = DataTable.apply("request for", "privilege", "requested", "id");

        if (accessRequests.isCanManageAccessRequests()) {
            for (DatasetAccessRequestVM request : accessRequests.getUserRequests()) {
                grants = grants.withRow(
                    request.getGrantFor(),
                    request.getGrant(),
                    request.getInitiated(),
                    request.getId());
            }
        }

        out.println("PROPERTIES");
        out.println("----------");
        out.println(properties.toAscii(false, true));
        out.println();

        if (canViewMembers) {
            out.println("MEMBERS");
            out.println("-------");
            out.println(acl.toAscii());
        }

        if (accessRequests.isCanManageAccessRequests()) {
            out.println();
            out.println("OPEN ACCESS REQUESTS");
            out.println("--------------------");
            out.println(grants.toAscii());
        }

        return CommandResult.success(sw.toString(), acl, grants);
    }
}
