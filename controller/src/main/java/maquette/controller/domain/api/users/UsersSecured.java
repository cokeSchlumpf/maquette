package maquette.controller.domain.api.users;

import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.notifcation.protocol.NotificationsMessage;
import maquette.controller.domain.entities.notifcation.protocol.queries.GetNotification;
import maquette.controller.domain.entities.notifcation.protocol.results.GetNotificationResult;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.entities.user.protocol.queries.GetDetails;
import maquette.controller.domain.entities.user.protocol.results.GetDetailsResult;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.exceptions.NotAuthorizedException;
import maquette.controller.domain.values.iam.Token;
import maquette.controller.domain.values.iam.TokenAuthenticatedUser;
import maquette.controller.domain.values.iam.TokenDetails;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserDetails;
import maquette.controller.domain.values.iam.UserId;
import maquette.controller.domain.values.notification.Notification;

@AllArgsConstructor(staticName = "apply")
final class UsersSecured implements Users {

    private final Users delegate;

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
        return delegate.authenticate(id, secret);
    }

    @Override
    public CompletionStage<Notification> markNotificationAsRead(User executor, UID notification) {
        return patterns
            .ask(
                notifications,
                (replyTo, errorTo) -> GetNotification.apply(executor, notification, replyTo, errorTo),
                GetNotificationResult.class)
            .thenApply(GetNotificationResult::getNotification)
            .thenApply(nf -> nf.getTo().hasAuthorization(executor))
            .thenCompose(canDo -> {
              if (canDo) {
                  return delegate.markNotificationAsRead(executor, notification);
              } else {
                  throw NotAuthorizedException.apply(executor);
              }
            });
    }

    @Override
    public CompletionStage<Set<Notification>> getNotifications(User executor) {
        return delegate
            .getNotifications(executor)
            .thenApply(notifications -> notifications
                .stream()
                .filter(notification -> notification.getTo().hasAuthorization(executor))
                .collect(Collectors.toSet()));
    }

    @Override
    public CompletionStage<Set<TokenDetails>> getTokens(User executor, UserId forUser) {
        return getDetails(forUser)
            .thenCompose(details -> {
                if (details.canViewPersonalProperties(executor)) {
                    return delegate.getTokens(executor, forUser);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<Token> registerToken(User executor, UserId forUser, ResourceName name) {
        return getDetails(forUser)
            .thenCompose(details -> {
                if (details.canManageTokens(executor)) {
                    return delegate.registerToken(executor, forUser, name);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<Token> renewAccessToken(User executor, UserId forUser, ResourceName name) {
        return getDetails(forUser)
            .thenCompose(details -> {
                if (details.canManageTokens(executor)) {
                    return delegate.renewAccessToken(executor, forUser, name);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<Done> deleteAccessToken(User executor, UserId forUser, ResourceName name) {
        return getDetails(forUser)
            .thenCompose(details -> {
                if (details.canManageTokens(executor)) {
                    return delegate.deleteAccessToken(executor, forUser, name);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

}
