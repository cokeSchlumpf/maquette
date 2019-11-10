package maquette.controller.domain.api.commands.views;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.ViewModel;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetGrant;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatasetVM implements ViewModel {

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

    @JsonProperty(MEMBERS)
    private final List<MembersEntryVM> members;

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
        @JsonProperty(MEMBERS) List<MembersEntryVM> members) {

        return new DatasetVM(
            project, dataset, description, owner, isPrivate, requiresApproval, classification,
            createdBy, created, modifiedBy, modified, ImmutableList.copyOf(members));
    }

    public static DatasetVM apply(DatasetDetails details, OutputFormat of) {
        List<MembersEntryVM> members = Lists.newArrayList();
        for (DatasetGrant grant : details.getAcl().getGrants()) {
            members.add(MembersEntryVM.apply(grant, of));
        }

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
            members);
    }

}
