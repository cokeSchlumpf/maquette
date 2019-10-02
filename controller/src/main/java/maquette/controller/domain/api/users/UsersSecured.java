package maquette.controller.domain.api.users;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.entities.user.protocol.queries.GetDetails;
import maquette.controller.domain.entities.user.protocol.results.GetDetailsResult;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.exceptions.NotAuthorizedException;
import maquette.controller.domain.values.iam.Token;
import maquette.controller.domain.values.iam.TokenAuthenticatedUser;
import maquette.controller.domain.values.iam.TokenDetails;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserDetails;
import maquette.controller.domain.values.iam.UserId;

@AllArgsConstructor(staticName = "apply")
final class UsersSecured implements Users {

    private final Users delegate;

    private final ActorRef<ShardingEnvelope<UserMessage>> users;

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
    public CompletionStage<DatasetDetails> createDataset(User executor, ResourceName dataset, boolean isPrivate) {
        return delegate.createDataset(executor, dataset, isPrivate);
    }

    @Override
    public CompletionStage<Done> deleteDataset(User executor, ResourceName dataset) {
        return delegate.deleteDataset(executor, dataset);
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> getDatasets(User executor) {
        // TODO: Other users can read datasets of another user if they are not private.
        return delegate.getDatasets(executor);
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
            .thenCompose(details ->  {
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
            .thenCompose(details ->  {
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
            .thenCompose(details ->  {
                if (details.canManageTokens(executor)) {
                    return delegate.deleteAccessToken(executor, forUser, name);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

}
