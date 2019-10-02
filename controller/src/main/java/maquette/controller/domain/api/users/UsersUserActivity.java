package maquette.controller.domain.api.users;

import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.controller.domain.services.CreateDefaultNamespace;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.Token;
import maquette.controller.domain.values.iam.TokenAuthenticatedUser;
import maquette.controller.domain.values.iam.TokenDetails;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserId;

@AllArgsConstructor(staticName = "apply")
public final class UsersUserActivity implements Users {

    private final Users delegate;

    private final CreateDefaultNamespace createDefaultNamespace;

    private <T> CompletionStage<T> createDefaultNamespace(User executor, Function<Users, CompletionStage<T>> andThen) {
        return createDefaultNamespace.run(executor, () -> andThen.apply(delegate));
    }

    @Override
    public CompletionStage<TokenAuthenticatedUser> authenticate(UserId id, UID secret) {
        return delegate.authenticate(id, secret);
    }

    @Override
    public CompletionStage<DatasetDetails> createDataset(User executor, ResourceName dataset, boolean isPrivate) {
        return createDefaultNamespace(executor, u -> u.createDataset(executor, dataset, isPrivate));
    }

    @Override
    public CompletionStage<Done> deleteDataset(User executor, ResourceName dataset) {
        return createDefaultNamespace(executor, u -> u.deleteDataset(executor, dataset));
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> getDatasets(User executor) {
        return createDefaultNamespace(executor, u -> u.getDatasets(executor));
    }

    @Override
    public CompletionStage<Set<TokenDetails>> getTokens(User executor, UserId forUser) {
        return createDefaultNamespace(executor, u -> u.getTokens(executor, forUser));
    }

    @Override
    public CompletionStage<Token> registerToken(User executor, UserId forUser, ResourceName name) {
        return createDefaultNamespace(executor, u -> u.registerToken(executor, forUser, name));
    }

    @Override
    public CompletionStage<Token> renewAccessToken(User executor, UserId forUser, ResourceName name) {
        return createDefaultNamespace(executor, u -> u.renewAccessToken(executor, forUser, name));
    }

    @Override
    public CompletionStage<Done> deleteAccessToken(User executor, UserId forUser, ResourceName name) {
        return createDefaultNamespace(executor, u -> u.deleteAccessToken(executor, forUser, name));
    }

}
