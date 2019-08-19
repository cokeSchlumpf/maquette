package maquette.controller.domain.entities.namespace.protocol.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.Message;
import maquette.controller.domain.values.namespace.NamespaceInfo;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetNamespaceInfoResult implements Message {

    private static final String NAMESPACE_INFO = "namespace-info";

    @JsonProperty(NAMESPACE_INFO)
    private final NamespaceInfo namespaceInfo;

    @JsonCreator
    public static GetNamespaceInfoResult apply(
        @JsonProperty(NAMESPACE_INFO) NamespaceInfo namespaceInfo) {

        return new GetNamespaceInfoResult(namespaceInfo);
    }

}
