package maquette.controller.domain.entities.namespace.protocol.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.GrantedAuthorization;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangedOwner {

    private static final String NAMESPACE = "namespace";
    private static final String NEW_OWNER = "new-owner";

    @JsonProperty(NAMESPACE)
    private final ResourceName namespace;

    @JsonProperty(NEW_OWNER)
    private final GrantedAuthorization newOwner;

    @JsonCreator
    public static ChangedOwner apply(
        @JsonProperty(NAMESPACE) ResourceName namespace,
        @JsonProperty(NEW_OWNER) GrantedAuthorization newOwner) {

        return new ChangedOwner(namespace, newOwner);
    }

}
