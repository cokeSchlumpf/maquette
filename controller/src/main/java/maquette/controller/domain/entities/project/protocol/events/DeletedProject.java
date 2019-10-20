package maquette.controller.domain.entities.project.protocol.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.ProjectEvent;
import maquette.controller.domain.entities.project.protocol.ProjectsEvent;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeletedProject implements ProjectEvent, ProjectsEvent {

    private static final String NAME = "name";
    private static final String DELETED_BY = "deleted-by";
    private static final String DELETED_AT = "deleted-at";

    @JsonProperty(NAME)
    private final ResourceName name;

    @JsonProperty(DELETED_BY)
    private final UserId deletedBy;

    @JsonProperty(DELETED_AT)
    private final Instant deletedAt;

    @JsonCreator
    public static DeletedProject apply(
        @JsonProperty(NAME) ResourceName name,
        @JsonProperty(DELETED_BY) UserId deletedBy,
        @JsonProperty(DELETED_AT) Instant deletedAt) {

        return new DeletedProject(name, deletedBy, deletedAt);
    }


}
