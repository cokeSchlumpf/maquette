package maquette.controller.domain.api.commands.views;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.ViewModel;
import maquette.controller.domain.values.project.ProjectDetails;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectVM implements ViewModel {

    private static final String NAME = "name";
    private static final String CREATED = "created";
    private static final String CREATED_BY = "created-by";
    private static final String MODIFIED = "modified";
    private static final String MODIFIED_BY = "modified-by";
    private static final String DESCRIPTION = "description";
    private static final String PRIVATE = "private";
    private static final String OWNER = "owner";
    private static final String MEMBERS = "members";

    @JsonProperty(NAME)
    private final String name;

    @JsonProperty(DESCRIPTION)
    private final String description;

    @JsonProperty(PRIVATE)
    private boolean isPrivate;

    @JsonProperty(OWNER)
    private final AuthorizationVM owner;

    @JsonProperty(CREATED)
    private final String created;

    @JsonProperty(CREATED_BY)
    private final String createdBy;

    @JsonProperty(MODIFIED)
    private final String modified;

    @JsonProperty(MODIFIED_BY)
    private final String modifiedBy;

    @JsonProperty(MEMBERS)
    private final List<MembersEntryVM> members;

    @JsonCreator
    public static ProjectVM apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(PRIVATE) boolean isPrivate,
        @JsonProperty(OWNER) AuthorizationVM owner,
        @JsonProperty(CREATED) String created,
        @JsonProperty(CREATED_BY) String createdBy,
        @JsonProperty(MODIFIED) String modified,
        @JsonProperty(MODIFIED_BY) String modifiedBy,
        @JsonProperty(MEMBERS) List<MembersEntryVM> members) {

        return new ProjectVM(
            name, description, isPrivate, owner, created, createdBy,
            modified, modifiedBy, ImmutableList.copyOf(members));
    }

    public static ProjectVM apply(
        ProjectDetails details, List<MembersEntryVM> members, OutputFormat of) {

        return ProjectVM.apply(
            of.format(details.getName()),
            details.getDescription().asHTMLString(),
            details.getAcl().isPrivate(),
            AuthorizationVM.apply(details.getAcl().getOwner()),
            of.format(details.getCreated()),
            of.format(details.getCreatedBy()),
            of.format(details.getModified()),
            of.format(details.getModifiedBy()),
            members);
    }

}
