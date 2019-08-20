package maquette.controller.domain.values.namespace;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.UserId;

@Value
@Wither
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NamespaceDetails {

    private static final String ACL = "acl";
    private static final String CREATED = "created";
    private static final String CREATED_BY = "created-by";
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

    @JsonCreator
    public static NamespaceDetails apply(
        @JsonProperty(NAME) ResourceName name,
        @JsonProperty(CREATED_BY) UserId createdBy,
        @JsonProperty(CREATED) Instant created,
        @JsonProperty(MODIFIED_BY) UserId modifiedBy,
        @JsonProperty(MODIFIED) Instant modified,
        @JsonProperty(ACL) NamespaceACL acl) {

        return new NamespaceDetails(name, createdBy, created, modifiedBy, modified, acl);
    }

}
