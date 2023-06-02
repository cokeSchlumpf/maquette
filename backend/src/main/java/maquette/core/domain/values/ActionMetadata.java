package maquette.core.domain.values;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.domain.users.AnonymousUser;
import maquette.core.domain.users.AuthenticatedUser;
import maquette.core.domain.users.RegisteredUser;
import maquette.core.domain.users.User;

import java.time.Instant;

/**
 * Simple value class which stores base information about actions (e.g. for modified or created fields).
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ActionMetadata {

    private static final String BY = "by";
    private static final String AT = "at";

    /**
     * The user who executed the action. The value should contain a unique, immutable user id.
     */
    @JsonProperty(BY)
    String by;

    /**
     * The moment when the action was executed.
     */
    @JsonProperty(AT)
    Instant at;

    /**
     * @param by The user who executed the action. The value should contain a unique, immutable user id.
     * @param at The moment when the action was executed.
     * @return A new instance
     */
    @JsonCreator
    public static ActionMetadata apply(
        @JsonProperty(BY) String by,
        @JsonProperty(AT) Instant at) {

        return new ActionMetadata(by, at);
    }

    /**
     * Creates a new instance.
     *
     * @param by The user who executed the action.
     * @return A new instance.
     */
    public static ActionMetadata apply(User by) {
        return apply(by, Instant.now());
    }

    /**
     * Creates a new instance.
     *
     * @param by The unique and immutable user id for the user who executed the action.
     * @return A new instance.
     */
    public static ActionMetadata apply(String by) {
        return apply(by, Instant.now());
    }

    /**
     * Creates a new instance.
     *
     * @param user The user who executed the action.
     * @param at   The moment when the action was executed.
     * @return A new instance.
     */
    public static ActionMetadata apply(User user, Instant at) {
        return apply(user.getDisplayName(), at);
    }

}
