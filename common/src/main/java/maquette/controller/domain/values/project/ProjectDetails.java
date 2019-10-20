package maquette.controller.domain.values.project;

import java.time.Instant;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectDetails {

    private static final String ACL = "acl";
    private static final String CREATED = "created";
    private static final String CREATED_BY = "created-by";
    private static final String DATASETS = "datasets";
    private static final String DESCRIPTION = "description";
    private static final String MODIFIED = "modified";
    private static final String MODIFIED_BY = "modified-by";
    private static final String NAME = "name";

    @JsonProperty(NAME)
    private final ResourceName name;

    @JsonProperty(CREATED_BY)
    private final UserId createdBy;

    @JsonProperty(CREATED)
    private final Instant created;

    @JsonProperty(DESCRIPTION)
    private final Markdown description;

    @JsonProperty(MODIFIED_BY)
    private final UserId modifiedBy;

    @JsonProperty(MODIFIED)
    private final Instant modified;

    @JsonProperty(ACL)
    private final ProjectACL acl;

    @JsonProperty(DATASETS)
    private final Set<ResourceName> datasets;

    @JsonCreator
    public static ProjectDetails apply(
        @JsonProperty(NAME) ResourceName name,
        @JsonProperty(CREATED_BY) UserId createdBy,
        @JsonProperty(CREATED) Instant created,
        @JsonProperty(DESCRIPTION) Markdown description,
        @JsonProperty(MODIFIED_BY) UserId modifiedBy,
        @JsonProperty(MODIFIED) Instant modified,
        @JsonProperty(ACL) ProjectACL acl,
        @JsonProperty(DATASETS) Set<ResourceName> datasets) {

        return new ProjectDetails(name, createdBy, created, description, modifiedBy, modified, acl, ImmutableSet.copyOf(datasets));
    }

    public ProjectDetails withDescription(Markdown description) {
        return apply(name, createdBy, created, description, modifiedBy, modified, acl, datasets);
    }

    public ProjectDetails withName(ResourceName name) {
        return apply(name, createdBy, created, description, modifiedBy, modified, acl, datasets);
    }

    public ProjectDetails withModified(UserId modifiedBy, Instant modified) {
        return apply(name, createdBy, created, description, modifiedBy, modified, acl, datasets);
    }

    public ProjectDetails withAcl(ProjectACL acl) {
        return apply(name, createdBy, created, description, modifiedBy, modified, acl, datasets);
    }

    public ProjectDetails withDatasets(Set<ResourceName> datasets) {
        return apply(name, createdBy, created, description, modifiedBy, modified, acl, datasets);
    }

}
