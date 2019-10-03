package maquette.controller.domain.api.users;

import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.services.CollectDatasets;
import maquette.controller.domain.services.CollectNamespaceInfos;
import maquette.controller.domain.services.NamespaceServices;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
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
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.exceptions.NoUserNamespaceDefinedException;
import maquette.controller.domain.values.iam.Token;
import maquette.controller.domain.values.iam.TokenAuthenticatedUser;
import maquette.controller.domain.values.iam.TokenDetails;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserDetails;
import maquette.controller.domain.values.iam.UserId;
import maquette.controller.domain.values.namespace.NamespaceInfo;

@AllArgsConstructor(staticName = "apply")
final class UsersImpl implements Users {

    private final ActorRef<ShardingEnvelope<UserMessage>> users;

    private final ActorRef<NamespacesMessage> namespacesRegistry;

    private final ActorRef<ShardingEnvelope<NamespaceMessage>> namespaces;

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

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

    private CompletionStage<ResourceName> getNamespace(UserId forUser) {
        return getDetails(forUser)
            .thenApply(details -> details.getNamespace().orElseThrow(() -> NoUserNamespaceDefinedException.apply(forUser)));
    }

    private CompletionStage<NamespaceServices> withNamespace(UserId forUser) {
        return getNamespace(forUser)
            .thenApply(namespace -> NamespaceServices.apply(namespacesRegistry, namespaces, datasets, patterns, namespace));
    }

    @Override
    public CompletionStage<DatasetDetails> createDataset(User executor, ResourceName dataset, boolean isPrivate) {
        return withNamespace(executor.getUserId())
            .thenCompose(ns -> ns.createDataset(executor, dataset, isPrivate));
    }

    @Override
    public CompletionStage<Done> deleteDataset(User executor, ResourceName dataset) {
        return withNamespace(executor.getUserId())
            .thenCompose(ns -> ns.deleteDataset(executor, dataset));
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> getDatasets(User executor) {
        CompletionStage<Set<NamespaceInfo>> namespaceInfos = patterns
            .process(result -> CollectNamespaceInfos.create(namespacesRegistry, namespaces, result));

        CompletionStage<Set<DatasetDetails>> allDatasets = namespaceInfos
            .thenCompose(infos -> patterns.process(result -> CollectDatasets.create(infos, datasets, result)));

        return allDatasets.thenApply(datasets -> datasets
            .stream()
            .filter(ds -> ds.getAcl().canManage(executor))
            .collect(Collectors.toSet()));
    }

    @Override
    public CompletionStage<TokenAuthenticatedUser> authenticate(UserId id, UID secret) {
        return getDetails(id).thenApply(details -> details.authenticateWithToken(secret));
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
