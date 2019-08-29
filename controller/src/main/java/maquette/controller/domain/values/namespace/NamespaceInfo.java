package maquette.controller.domain.values.namespace;

import java.time.Instant;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ResourceName;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NamespaceInfo {

    private static final String ACL = "acl";
    private static final String MODIFIED = "modified";
    private static final String NAME = "name";
    private static final String DATASETS = "datasets";

    @JsonProperty(NAME)
    private final ResourceName name;

    @JsonProperty(MODIFIED)
    private final Instant modified;

    @JsonProperty(ACL)
    private final NamespaceACL acl;

    @JsonProperty(DATASETS)
    private final Set<ResourceName> datasets;

    @JsonCreator
    public static NamespaceInfo apply(
        @JsonProperty(NAME) ResourceName name,
        @JsonProperty(MODIFIED) Instant modified,
        @JsonProperty(ACL) NamespaceACL acl,
        @JsonProperty(DATASETS) Set<ResourceName> datasets) {

        return new NamespaceInfo(name, modified, acl, ImmutableSet.copyOf(datasets));
    }

}
