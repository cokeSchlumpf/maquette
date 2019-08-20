package maquette.controller.domain.entities.namespace.protocol.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.Message;
import maquette.controller.domain.values.namespace.NamespaceDetails;
import maquette.controller.domain.values.namespace.NamespaceInfo;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetNamespaceDetailsResult implements Message {

    private static final String NAMESPACE_DETAILS = "namespace-details";

    @JsonProperty(NAMESPACE_DETAILS)
    private final NamespaceDetails namespaceDetails;

    @JsonCreator
    public static GetNamespaceDetailsResult apply(
        @JsonProperty(NAMESPACE_DETAILS) NamespaceDetails namespaceInfo) {

        return new GetNamespaceDetailsResult(namespaceInfo);
    }

}
