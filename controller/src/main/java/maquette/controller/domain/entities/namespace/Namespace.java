package maquette.controller.domain.entities.namespace;

import maquette.controller.domain.values.core.ResourceName;

public class Namespace {

    public static String createEntityId(ResourceName namespaceName) {
        return namespaceName.getValue();
    }

}
