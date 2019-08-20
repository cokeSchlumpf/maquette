package maquette.controller.domain.entities.namespace.protocol.results;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.Message;
import maquette.controller.domain.values.core.ResourceName;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ListNamespacesResult implements Message {

    private static final String NAMESPACES = "namespaces";

    @JsonProperty(NAMESPACES)
    private final Set<ResourceName> namespaces;

    @JsonCreator
    public static ListNamespacesResult apply(
        @JsonProperty(NAMESPACES) Set<ResourceName> namespaces) {

        return new ListNamespacesResult(ImmutableSet.copyOf(namespaces));
    }

}
