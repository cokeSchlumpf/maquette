package maquette.controller.domain.api.users;

import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.notifcation.protocol.NotificationsMessage;
import maquette.controller.domain.entities.notifcation.protocol.commands.MarkNotificationAsRead;
import maquette.controller.domain.entities.notifcation.protocol.events.MarkedNotificationAsRead;
import maquette.controller.domain.entities.notifcation.protocol.queries.GetNotification;
import maquette.controller.domain.entities.notifcation.protocol.queries.GetNotifications;
import maquette.controller.domain.entities.notifcation.protocol.results.GetNotificationResult;
import maquette.controller.domain.entities.notifcation.protocol.results.GetNotificationsResult;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.entities.user.protocol.commands.RegisterAccessToken;
import maquette.controller.domain.entities.user.protocol.commands.RemoveAccessToken;
import maquette.controller.domain.entities.user.protocol.commands.RenewAccessTokenSecret;
import maquette.controller.domain.entities.user.protocol.events.RegisteredAccessToken;
import maquette.controller.domain.entities.user.protocol.events.RemovedAccessToken;
import maquette.controller.domain.entities.user.protocol.queries.GetDetails;
import maquette.controller.domain.entities.user.protocol.results.GetDetailsResult;
import maquette.controller.domain.util.ActorPatterns;
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

@AllArgsConstructor(staticName = "apply")
final class UsersImpl implements Users {

    private final ActorRef<ShardingEnvelope<UserMessage>> users;

    private final ActorRef<NotificationsMessage> notifications;

    private final ActorPatterns patterns;

    private CompletionStage<UserDetails> getDetails(UserId forUser) {
        return patterns
            .ask(
                users,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    maquette.controller.domain.entities.user.User.createEntityId(forUser),
                    GetDetails.apply(replyTo, errorTo)),
                GetDetailsResult.class)
            .thenApply(GetDetailsResult::getDetails);
    }

    @Override
    public CompletionStage<TokenAuthenticatedUser> authenticate(UserId id, UID secret) {
        return getDetails(id).thenApply(details -> details.authenticateWithToken(secret));
    }

    @Override
    public CompletionStage<Notification> markNotificationAsRead(User executor, UID notification) {
        return patterns
            .ask(
                notifications,
                (replyTo, errorTo) -> MarkNotificationAsRead.apply(executor, notification, replyTo, errorTo),
                MarkedNotificationAsRead.class)
            .thenCompose(marked -> patterns.ask(
                notifications,
                (replyTo, errorTo) -> GetNotification.apply(executor, marked.getNotification(), replyTo, errorTo),
                GetNotificationResult.class))
            .thenApply(GetNotificationResult::getNotification);
    }

    @Override
    public CompletionStage<PersonalUserProfile> getPersonalUserProfile(User executor) {
        return getDetails(executor.getUserId())
            .thenApply(details -> PersonalUserProfile.apply(
                details.getId(),
                details.getDatasetAccessRequests(),
                details.getNamespace().orElse(null)));
    }

    @Override
    public CompletionStage<Set<Notification>> getNotifications(User executor) {
        return patterns
            .ask(
                notifications,
                (replyTo, errorTo) -> GetNotifications.apply(executor, replyTo, errorTo),
                GetNotificationsResult.class)
            .thenApply(GetNotificationsResult::getNotifications);
    }

    @Override
    public CompletionStage<Set<TokenDetails>> getTokens(User executor, UserId forUser) {
        return getDetails(forUser)
            .thenApply(details -> details
                .getAccessTokens()
                .stream()
                .map(Token::getDetails)
                .collect(Collectors.toSet()));
    }

    @Override
    public CompletionStage<Token> registerToken(User executor, UserId forUser, ResourceName name) {
        return patterns
            .ask(
                users,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    maquette.controller.domain.entities.user.User.createEntityId(forUser),
                    RegisterAccessToken.apply(executor, name, replyTo, errorTo)),
                RegisteredAccessToken.class)
            .thenApply(RegisteredAccessToken::getToken);
    }

    @Override
    public CompletionStage<Token> renewAccessToken(User executor, UserId forUser, ResourceName name) {
        return patterns
            .ask(
                users,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    maquette.controller.domain.entities.user.User.createEntityId(forUser),
                    RenewAccessTokenSecret.apply(executor, name, replyTo, errorTo)),
                RegisteredAccessToken.class)
            .thenApply(RegisteredAccessToken::getToken);
    }

    @Override
    public CompletionStage<Done> deleteAccessToken(User executor, UserId forUser, ResourceName name) {
        return patterns
            .ask(
                users,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    maquette.controller.domain.entities.user.User.createEntityId(forUser),
                    RemoveAccessToken.apply(executor, name, replyTo, errorTo)),
                RemovedAccessToken.class)
            .thenApply(removed -> Done.getInstance());
    }

}
