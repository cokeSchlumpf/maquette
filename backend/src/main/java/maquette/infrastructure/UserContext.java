package maquette.infrastructure;

import maquette.core.domain.users.AnonymousUser;
import maquette.core.domain.users.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 * This component manages the user of the current session. The value is set by an authentication handler which
 * might get the information from a request (e.g. JWT, other Request Headers, Cookies, etc.).
 * <p>
 * When user information is needed in another component, this one can be injected.
 * <p>
 * The <code>updated</code>-Property might be used to check whether user information needs to be extracted
 * from request or not.
 */
@Component
@SessionScope
public class UserContext {

    private User user;

    private boolean updated;

    public UserContext() {
        this.user = AnonymousUser.apply();
        this.updated = false;
    }

    /**
     * Whether a user has been set or not during the current session.
     *
     * @return True if user has been set, False if context is in inital state.
     */
    public boolean isUpdated() {
        return updated;
    }

    /**
     * Updates the user context.
     *
     * @param user The user as detected based on request.
     */
    public void setUser(User user) {
        this.user = user;
        this.updated = true;
    }

    /**
     * Get the current detected user.
     *
     * @return The user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Reset the user context to its initial state, e.g. on logout.
     */
    public void clear() {
        this.user = AnonymousUser.apply();
        this.updated = false;
    }

}
