package maquette.controller.domain.api.users;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import akka.Done;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.Token;
import maquette.controller.domain.values.iam.TokenAuthenticatedUser;
import maquette.controller.domain.values.iam.TokenDetails;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserId;

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
     * Creates a dataset in the users (executors) namespace.
     *
     * @param executor
     *     The user which executes the command
     * @param dataset
     *     The name of the dataset to be created
     * @param isPrivate
     *     Whether the dataset is private or not
     * @return The details of the newly created dataset
     */
    @Deprecated
    CompletionStage<DatasetDetails> createDataset(User executor, ResourceName dataset, boolean isPrivate);

    /**
     * Deletes a dataset in the users (executors) namespace.
     *
     * @param executor
     *     The user which executes the command
     * @param dataset
     *     The name of the dataset to be deleted
     * @return Just Done.
     */
    @Deprecated
    CompletionStage<Done> deleteDataset(User executor, ResourceName dataset);

    CompletionStage<Set<DatasetDetails>> getDatasets(User executor);

    CompletionStage<Set<TokenDetails>> getTokens(User executor, UserId forUser);

    CompletionStage<Token> registerToken(User executor, UserId forUser, ResourceName name);

    CompletionStage<Token> renewAccessToken(User executor, UserId forUser, ResourceName name);

    CompletionStage<Done> deleteAccessToken(User executor, UserId forUser, ResourceName name);

}
