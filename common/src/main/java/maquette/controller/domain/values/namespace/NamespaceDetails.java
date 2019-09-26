package maquette.controller.domain.values.namespace;

import java.time.Instant;
import java.util.Optional;
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
public class NamespaceDetails {

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

    @JsonProperty(MODIFIED_BY)
    private final UserId modifiedBy;

    @JsonProperty(MODIFIED)
    private final Instant modified;

    @JsonProperty(ACL)
    private final NamespaceACL acl;

    @JsonProperty(DATASETS)
    private final Set<ResourceName> datasets;

    @JsonProperty(DESCRIPTION)
    private final Markdown description;

    @JsonCreator
    public static NamespaceDetails apply(
        @JsonProperty(NAME) ResourceName name,
        @JsonProperty(CREATED_BY) UserId createdBy,
        @JsonProperty(CREATED) Instant created,
        @JsonProperty(MODIFIED_BY) UserId modifiedBy,
        @JsonProperty(MODIFIED) Instant modified,
        @JsonProperty(ACL) NamespaceACL acl,
        @JsonProperty(DATASETS) Set<ResourceName> datasets,
        @JsonProperty(DESCRIPTION) Markdown description) {

        return new NamespaceDetails(name, createdBy, created, modifiedBy, modified, acl, ImmutableSet.copyOf(datasets), description);
    }

    public static NamespaceDetails apply(
        ResourceName name, UserId createdBy, Instant created, UserId modifiedBy,
        Instant modified, NamespaceACL acl, Set<ResourceName> datasets) {

        return apply(name, createdBy, created, modifiedBy, modified, acl, datasets, null);
    }

    public Optional<Markdown> getDescription() {
        return Optional.ofNullable(description);
    }

    public NamespaceDetails withName(ResourceName name) {
        return apply(name, createdBy, created, modifiedBy, modified, acl, datasets, description);
    }

    public NamespaceDetails withCreatedBy(UserId createdBy) {
        return apply(name, createdBy, created, modifiedBy, modified, acl, datasets, description);
    }

    public NamespaceDetails withCreated(Instant created) {
        return apply(name, createdBy, created, modifiedBy, modified, acl, datasets, description);
    }

    public NamespaceDetails withDescription(Markdown description) {
        return apply(name, createdBy, created, modifiedBy, modified, acl, datasets, description);
    }

    public NamespaceDetails withModifiedBy(UserId modifiedBy) {
        return apply(name, createdBy, created, modifiedBy, modified, acl, datasets, description);
    }

    public NamespaceDetails withModified(Instant modified) {
        return apply(name, createdBy, created, modifiedBy, modified, acl, datasets, description);
    }

    public NamespaceDetails withAcl(NamespaceACL acl) {
        return apply(name, createdBy, created, modifiedBy, modified, acl, datasets, description);
    }

    public NamespaceDetails withDatasets(Set<ResourceName> datasets) {
        return apply(name, createdBy, created, modifiedBy, modified, acl, datasets, description);
    }

}
