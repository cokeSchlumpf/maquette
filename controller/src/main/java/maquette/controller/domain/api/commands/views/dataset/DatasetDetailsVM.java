package maquette.controller.domain.api.commands.views.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.views.AuthorizationVM;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasetDetailsVM {

    private static final String CLASSIFICATION = "classification";
    private static final String CREATED = "created";
    private static final String CREATED_BY = "created-by";
    private static final String DESCRIPTION = "description";
    private static final String MODIFIED = "modified";
    private static final String MODIFIED_BY = "modified-by";
    private static final String OWNER = "owner";
    private static final String PRIVATE = "private";
    private static final String REQUIRES_APPROVAL = "requires-approval";

    @JsonProperty(DESCRIPTION)
    private final String description;

    @JsonProperty(OWNER)
    private final AuthorizationVM owner;

    @JsonProperty(PRIVATE)
    private final String isPrivate;

    @JsonProperty(REQUIRES_APPROVAL)
    private final String requiresApproval;

    @JsonProperty(CLASSIFICATION)
    private final String classification;

    @JsonProperty(CREATED_BY)
    private final String createdBy;

    @JsonProperty(CREATED)
    private final String created;

    @JsonProperty(MODIFIED_BY)
    private final String modifiedBy;

    @JsonProperty(MODIFIED)
    private final String modified;

    @JsonCreator
    public static DatasetDetailsVM apply(
        @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(OWNER) AuthorizationVM owner,
        @JsonProperty(PRIVATE) String isPrivate,
        @JsonProperty(REQUIRES_APPROVAL) String requiresApproval,
        @JsonProperty(CLASSIFICATION) String classification,
        @JsonProperty(CREATED_BY) String createdBy,
        @JsonProperty(CREATED) String created,
        @JsonProperty(MODIFIED_BY) String modifiedBy,
        @JsonProperty(MODIFIED) String modified) {

        return new DatasetDetailsVM(
            description, owner, isPrivate, requiresApproval, classification,
            createdBy, created, modifiedBy, modified);
    }

}
