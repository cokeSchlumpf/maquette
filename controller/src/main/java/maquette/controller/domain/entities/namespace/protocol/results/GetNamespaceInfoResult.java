package maquette.controller.domain.entities.namespace.protocol.results;

import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.Message;
import maquette.controller.domain.values.namespace.NamespaceInfo;

@Value
public class GetNamespaceInfoResult implements Message {

    private final NamespaceInfo namespaceInfo;

}
