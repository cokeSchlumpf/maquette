package maquette.controller.domain.api.views;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

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
import maquette.controller.domain.values.project.ProjectDetails;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectVM implements ViewModel {

    private static final String NAME = "name";
    private static final String CREATED = "created";
    private static final String CREATED_BY = "created-by";
    private static final String DATASETS = "datasets";
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

    @JsonProperty(DATASETS)
    private final int datasets;

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
        @JsonProperty(DATASETS) int datasets,
        @JsonProperty(MODIFIED) String modified,
        @JsonProperty(MODIFIED_BY) String modifiedBy,
        @JsonProperty(MEMBERS) List<MembersEntryVM> members) {

        return new ProjectVM(
            name, description, isPrivate, owner, created, createdBy,
            modified, modifiedBy, datasets, ImmutableList.copyOf(members));
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
            details.getDatasets().size(),
            of.format(details.getModified()),
            of.format(details.getModifiedBy()),
            members);
    }

    @Override
    public CommandResult toCommandResult(ObjectMapper om) {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);

        final DataTable properties = DataTable
            .apply("key", "property")
            .withRow("owner", owner)
            .withRow("private", isPrivate)
            .withRow("", "")
            .withRow("created", created)
            .withRow("created by", createdBy)
            .withRow("", "")
            .withRow("modified", modified)
            .withRow("modified by", modifiedBy)
            .withRow("", "")
            .withRow("datasets", datasets);

        DataTable acl = DataTable.apply("granted to", "privilege", "granted by", "granted at");

        for (MembersEntryVM grant : members) {
            acl = acl.withRow(
                grant.getGrantedTo(),
                grant.getPrivilege(),
                grant.getGrantedBy(),
                grant.getGrantedAt());
        }

        out.println(description);
        out.println();

        out.println("PROPERTIES");
        out.println("----------");
        out.println(properties.toAscii(false, true));
        out.println();
        out.println("ACCESS CONTROL");
        out.println("--------------");
        out.println(acl.toAscii());

        return CommandResult.success(sw.toString(), acl);
    }
}
