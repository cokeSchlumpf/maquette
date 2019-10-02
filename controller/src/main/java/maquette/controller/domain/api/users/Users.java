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

    CompletionStage<TokenAuthenticatedUser> authenticate(UserId id, UID secret);

    CompletionStage<DatasetDetails> createDataset(User executor, ResourceName dataset, boolean isPrivate);

    CompletionStage<Done> deleteDataset(User executor, ResourceName dataset);

    CompletionStage<Set<DatasetDetails>> getDatasets(User executor);

    CompletionStage<Set<TokenDetails>> getTokens(User executor, UserId forUser);

    CompletionStage<Token> registerToken(User executor, UserId forUser, ResourceName name);

    CompletionStage<Token> renewAccessToken(User executor, UserId forUser, ResourceName name);

    CompletionStage<Done> deleteAccessToken(User executor, UserId forUser, ResourceName name);

}
