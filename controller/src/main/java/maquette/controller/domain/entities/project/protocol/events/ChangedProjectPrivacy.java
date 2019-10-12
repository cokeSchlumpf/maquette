package maquette.controller.domain.entities.project.protocol.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.namespace.protocol.NamespaceEvent;
import maquette.controller.domain.entities.namespace.protocol.NamespacesEvent;
import maquette.controller.domain.entities.project.protocol.ProjectEvent;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangedProjectPrivacy implements ProjectEvent {

    private static final String IS_PRIVATE = "private";
    private static final String PROJECT = "project";
    private static final String CHANGED_AT = "changed-at";
    private static final String CHANGED_BY = "changed-by";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(IS_PRIVATE)
    private final boolean isPrivate;

    @JsonProperty(CHANGED_BY)
    private final UserId changedBy;

    @JsonProperty(CHANGED_AT)
    private final Instant changedAt;

    @JsonCreator
    public static ChangedProjectPrivacy apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(IS_PRIVATE) boolean isPrivate,
        @JsonProperty(CHANGED_BY) UserId changedBy,
        @JsonProperty(CHANGED_AT) Instant changedAt) {

        return new ChangedProjectPrivacy(project, isPrivate, changedBy, changedAt);
    }

}
