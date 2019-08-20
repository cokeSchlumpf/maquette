package maquette.controller.domain.values.namespace;

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

    private static final String NAME = "name";
    private static final String DATASETS = "datasets";

    @JsonProperty(NAME)
    private final ResourceName name;

    @JsonProperty(DATASETS)
    private final Set<ResourceName> datasets;

    @JsonCreator
    public static NamespaceInfo apply(
        @JsonProperty(NAME) ResourceName name,
        @JsonProperty(DATASETS) Set<ResourceName> datasets) {

        return new NamespaceInfo(name, ImmutableSet.copyOf(datasets));
    }

}
