package maquette.controller.domain.values.iam;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenDetails {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String CREATED = "created";
    private static final String CREATED_BY = "created-by";
    private static final String MODIFIED = "modified";
    private static final String MODIFIED_BY = "modified-by";
    private static final String OWNER = "owner";

    @JsonProperty(NAME)
    private final ResourceName name;

    @JsonProperty(ID)
    private final UID id;

    @JsonProperty(OWNER)
    private final UserId owner;

    @JsonProperty(CREATED)
    private final Instant created;

    @JsonProperty(CREATED_BY)
    private final UserId createdBy;

    @JsonProperty(MODIFIED)
    private final Instant modified;

    @JsonProperty(MODIFIED_BY)
    private final UserId modifiedBy;

    @JsonCreator
    public static TokenDetails apply(
        @JsonProperty(NAME) ResourceName name,
        @JsonProperty(ID) UID id,
        @JsonProperty(OWNER) UserId owner,
        @JsonProperty(CREATED) Instant created,
        @JsonProperty(CREATED_BY) UserId createdBy,
        @JsonProperty(MODIFIED) Instant modified,
        @JsonProperty(MODIFIED_BY) UserId modifiedBy) {

        return new TokenDetails(name, id, owner, created, createdBy, modified, modifiedBy);
    }

    public TokenDetails withModified(Instant modified, UserId modifiedBy) {
        return apply(name, id, owner, created, createdBy, modified, modifiedBy);
    }

}
