package maquette.controller.domain.api.users;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import akka.Done;
import maquette.controller.domain.entities.notifcation.Notifications;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.PersonalUserProfile;
import maquette.controller.domain.values.iam.Token;
import maquette.controller.domain.values.iam.TokenAuthenticatedUser;
import maquette.controller.domain.values.iam.TokenDetails;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserDetails;
import maquette.controller.domain.values.iam.UserId;
import maquette.controller.domain.values.notification.Notification;

public interface Users {

    /**
     * Authenticate a user based on a user token.
     *
     * @param id
     *     The user id of the user to check.
     * @param secret
     *     The secret token id.
     * @return An authenticated user if the user is successfully authenticated based on the token, otherwise exception will be thrown
     */
    CompletionStage<TokenAuthenticatedUser> authenticate(UserId id, UID secret);

    /**
     * Marks a notification as read by the user.
     *
     * @param executor
     *     The user which has read the notification
     * @param notification
     *     The id of the notification
     * @return The updated notification
     */
    CompletionStage<Notification> markNotificationAsRead(User executor, UID notification);

    /**
     * Return user details of the executor.
     *
     * @param executor
     *     The user which executes the request.
     * @return The personal user details.
     */
    CompletionStage<PersonalUserProfile> getPersonalUserProfile(User executor);

    /**
     * Returns the notifications of a user.
     *
     * @param executor
     *     The user for which the notifications should be received.
     * @return A list of all notifications which are addressed to the user.
     */
    CompletionStage<Set<Notification>> getNotifications(User executor);

    /**
     * Get tokens of a user.
     *
     * @param executor
     *     The user which executes the query
     * @param forUser
     *     The user for which tokens are queried
     * @return A set of existing tokens of the user
     */
    CompletionStage<Set<TokenDetails>> getTokens(User executor, UserId forUser);

    /**
     * Register a new token for a user.
     *
     * @param executor
     *     The user which executes the command
     * @param forUser
     *     The user for which the token should be registered
     * @param name
     *     The name of the token
     * @return The new token (including the secret)
     */
    CompletionStage<Token> registerToken(User executor, UserId forUser, ResourceName name);

    /**
     * Renew the secret of an existing user token.
     *
     * @param executor
     *     The user which executes the command
     * @param forUser
     *     The user for which the token should be renewed
     * @param name
     *     The name of the token
     * @return The token (including the secret)
     */
    CompletionStage<Token> renewAccessToken(User executor, UserId forUser, ResourceName name);

    /**
     * Delete an existing access token.
     *
     * @param executor
     *     The user which executes the command
     * @param forUser
     *     The user for which the token should be deleted
     * @param name
     *     The name of the token to be deleted
     * @return Just done
     */
    CompletionStage<Done> deleteAccessToken(User executor, UserId forUser, ResourceName name);

}
