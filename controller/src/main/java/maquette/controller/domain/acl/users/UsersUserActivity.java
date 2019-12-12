package maquette.controller.domain.acl.users;

import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.controller.domain.services.CreateDefaultProject;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.PersonalUserProfile;
import maquette.controller.domain.values.iam.Token;
import maquette.controller.domain.values.iam.TokenAuthenticatedUser;
import maquette.controller.domain.values.iam.TokenDetails;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserId;
import maquette.controller.domain.values.notification.Notification;

@AllArgsConstructor(staticName = "apply")
public final class UsersUserActivity implements Users {

    private final Users delegate;

    private final CreateDefaultProject createDefaultProject;

    private <T> CompletionStage<T> createDefaultProject(User executor, Function<Users, CompletionStage<T>> andThen) {
        return createDefaultProject.run(executor, () -> andThen.apply(delegate));
    }

    @Override
    public CompletionStage<TokenAuthenticatedUser> authenticate(UserId id, UID secret) {
        return delegate.authenticate(id, secret);
    }

    @Override
    public CompletionStage<Notification> markNotificationAsRead(User executor, UID notification) {
        return createDefaultProject(executor, u -> u.markNotificationAsRead(executor, notification));
    }

    @Override
    public CompletionStage<PersonalUserProfile> getPersonalUserProfile(User executor) {
        return createDefaultProject(executor, u -> u.getPersonalUserProfile(executor));
    }

    @Override
    public CompletionStage<Set<Notification>> getNotifications(User executor) {
        return createDefaultProject(executor, u -> u.getNotifications(executor));
    }

    @Override
    public CompletionStage<Set<TokenDetails>> getTokens(User executor, UserId forUser) {
        return createDefaultProject(executor, u -> u.getTokens(executor, forUser));
    }

    @Override
    public CompletionStage<Token> registerToken(User executor, UserId forUser, ResourceName name) {
        return createDefaultProject(executor, u -> u.registerToken(executor, forUser, name));
    }

    @Override
    public CompletionStage<Token> renewAccessToken(User executor, UserId forUser, ResourceName name) {
        return createDefaultProject(executor, u -> u.renewAccessToken(executor, forUser, name));
    }

    @Override
    public CompletionStage<Done> deleteAccessToken(User executor, UserId forUser, ResourceName name) {
        return createDefaultProject(executor, u -> u.deleteAccessToken(executor, forUser, name));
    }

}
