package maquette.controller.domain.entities.user.protocol.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.user.protocol.UserEvent;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RemovedAccessToken implements UserEvent {

    private static final String REMOVED_AT = "removed-at";
    private static final String REMOVED_BY = "removed-by";
    private static final String TOKEN = "token";

    @JsonProperty(REMOVED_AT)
    private final Instant removedAt;

    @JsonProperty(REMOVED_BY)
    private final UserId removedBy;

    @JsonProperty(TOKEN)
    private final ResourceName token;

    @JsonCreator
    public static RemovedAccessToken apply(
        @JsonProperty(REMOVED_AT)  Instant removedAt,
        @JsonProperty(REMOVED_BY) UserId removedBy,
        @JsonProperty(TOKEN) ResourceName token) {

        return new RemovedAccessToken(removedAt, removedBy, token);
    }

}
