package maquette.controller.domain.api.commands.views.dataset;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.CommandResult;
import maquette.controller.domain.api.commands.DataTable;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.ViewModel;
import maquette.controller.domain.api.commands.views.AuthorizationVM;
import maquette.controller.domain.api.commands.views.MembersEntryVM;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetMember;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatasetVM implements ViewModel {

    private static final String CAN_CREATE_ACCESS_REQUEST = "can-create-access-request";
    private static final String CAN_MANAGE_ACCESS_REQUESTS = "can-manage-access-requests";
    private static final String CLASSIFICATION = "classification";
    private static final String CREATED = "created";
    private static final String CREATED_BY = "created-by";
    private static final String DATASET = "dataset";
    private static final String DESCRIPTION = "description";
    private static final String MEMBERS = "members";
    private static final String MODIFIED = "modified";
    private static final String MODIFIED_BY = "modified-by";
    private static final String OWNER = "owner";
    private static final String PRIVATE = "private";
    private static final String PROJECT = "project";
    private static final String REQUIRES_APPROVAL = "requires-approval";
    private static final String USER_ACCESS_REQUESTS = "user-access-request";
    private static final String VERSIONS = "versions";

    @JsonProperty(PROJECT)
    private final String project;

    @JsonProperty(DATASET)
    private final String dataset;

    @JsonProperty(DESCRIPTION)
    private final String description;

    @JsonProperty(OWNER)
    private final AuthorizationVM owner;

    @JsonProperty(PRIVATE)
    private final String isPrivate;

    @JsonProperty(REQUIRES_APPROVAL)
    private final String requiresApproval;

    @JsonProperty(CLASSIFICATION)
    private final String classification;

    @JsonProperty(CREATED_BY)
    private final String createdBy;

    @JsonProperty(CREATED)
    private final String created;

    @JsonProperty(MODIFIED_BY)
    private final String modifiedBy;

    @JsonProperty(MODIFIED)
    private final String modified;

    @JsonProperty(VERSIONS)
    private final int versions;

    @JsonProperty(MEMBERS)
    private final List<MembersEntryVM> members;

    @JsonProperty(USER_ACCESS_REQUESTS)
    private final List<DatasetAccessRequestVM> accessRequests;

    @JsonProperty(CAN_CREATE_ACCESS_REQUEST)
    private final boolean canCreateAccessRequest;

    @JsonProperty(CAN_MANAGE_ACCESS_REQUESTS)
    private final boolean canManageAccessRequests;

    @JsonCreator
    public static DatasetVM apply(
        @JsonProperty(PROJECT) String project,
        @JsonProperty(DATASET) String dataset,
        @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(OWNER) AuthorizationVM owner,
        @JsonProperty(PRIVATE) String isPrivate,
        @JsonProperty(REQUIRES_APPROVAL) String requiresApproval,
        @JsonProperty(CLASSIFICATION) String classification,
        @JsonProperty(CREATED_BY) String createdBy,
        @JsonProperty(CREATED) String created,
        @JsonProperty(MODIFIED_BY) String modifiedBy,
        @JsonProperty(MODIFIED) String modified,
        @JsonProperty(VERSIONS) int versions,
        @JsonProperty(MEMBERS) List<MembersEntryVM> members,
        @JsonProperty(USER_ACCESS_REQUESTS) List<DatasetAccessRequestVM> accessRequests,
        @JsonProperty(CAN_CREATE_ACCESS_REQUEST) boolean canCreateAccessRequest,
        @JsonProperty(CAN_MANAGE_ACCESS_REQUESTS) boolean canManageAccessRequests) {

        return new DatasetVM(
            project, dataset, description, owner, isPrivate, requiresApproval, classification,
            createdBy, created, modifiedBy, modified, versions, ImmutableList.copyOf(members), accessRequests,
            canCreateAccessRequest, canManageAccessRequests);
    }

    public static DatasetVM apply(DatasetDetails details, User executor, OutputFormat of) {
        List<MembersEntryVM> members = details
            .getAcl()
            .getMembers()
            .stream()
            .map(grant -> MembersEntryVM.apply(grant, of))
            .collect(Collectors.toList());

        List<DatasetAccessRequestVM> accessRequests = details
            .getAcl()
            .getOpenGrants()
            .stream()
            .map(grant -> DatasetAccessRequestVM.apply(grant, details, executor, of))
            .collect(Collectors.toList());

        for (DatasetMember grant : details.getAcl().getMembers()) {
            members.add(MembersEntryVM.apply(grant, of));
        }

        boolean canCreateAccessRequest = !(details.getAcl().canConsume(executor) || details.getAcl().canProduce(executor));
        boolean canManageAccessRequests = details.getAcl().canManage(executor);

        return apply(
            of.format(details.getDataset().getProject()),
            of.format(details.getDataset().getName()),
            details.getDescription().orElse(Markdown.apply()).asPlainText(),
            AuthorizationVM.apply(details.getAcl().getOwner()),
            of.format(details.getAcl().isPrivate()),
            of.format(details.getGovernance().isApprovalRequired()),
            of.format(details.getGovernance().getClassification().name),
            of.format(details.getCreatedBy()),
            of.format(details.getCreated()),
            of.format(details.getModifiedBy()),
            of.format(details.getModified()),
            details.getVersions().size(),
            members,
            accessRequests,
            canCreateAccessRequest,
            canManageAccessRequests);
    }

    @Override
    public CommandResult toCommandResult(ObjectMapper om) {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);

        DataTable properties = DataTable
            .apply("key", "value")
            .withRow("owner", owner.getAuthorization())
            .withRow("requires approval", requiresApproval)
            .withRow("classification", classification)
            .withRow("", "")
            .withRow("created", created)
            .withRow("created by", createdBy)
            .withRow("", "")
            .withRow("modified", modified)
            .withRow("modified by", modifiedBy)
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

        if (description != null) {
            out.println(description);
            out.println();
        }

        DataTable grants = DataTable.apply("request for", "privilege", "requested", "id");

        for (DatasetAccessRequestVM request : accessRequests) {
            grants = grants.withRow(
                request.getGrantFor(),
                request.getGrant(),
                request.getInitiated(),
                request.getId());
        }

        out.println("PROPERTIES");
        out.println("----------");
        out.println(properties.toAscii(false, true));
        out.println();
        out.println("MEMBERS");
        out.println("-------");
        out.println(acl.toAscii());

        if (canManageAccessRequests) {
            out.println();
            out.println("OPEN ACCESS REQUESTS");
            out.println("--------------------");
            out.println(grants.toAscii());
        }

        return CommandResult.success(sw.toString(), acl, grants);
    }
}
