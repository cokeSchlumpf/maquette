package maquette.controller.domain.entities.user.state;

import java.time.Instant;
import java.util.Optional;

import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectFactories;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.user.protocol.UserEvent;
import maquette.controller.domain.entities.user.protocol.commands.RegisterAccessToken;
import maquette.controller.domain.entities.user.protocol.commands.RemoveAccessToken;
import maquette.controller.domain.entities.user.protocol.commands.RenewAccessTokenSecret;
import maquette.controller.domain.entities.user.protocol.events.RegisteredAccessToken;
import maquette.controller.domain.entities.user.protocol.events.RemovedAccessToken;
import maquette.controller.domain.entities.user.protocol.queries.GetDetails;
import maquette.controller.domain.entities.user.protocol.results.GetDetailsResult;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.Token;
import maquette.controller.domain.values.iam.TokenDetails;
import maquette.controller.domain.values.iam.UserDetails;

@AllArgsConstructor(staticName = "apply")
public final class ActiveUser implements State {

    private final EffectFactories<UserEvent, State> effect;

    private UserDetails details;

    @Override
    public Effect<UserEvent, State> onGetDetails(GetDetails get) {
        get.getReplyTo().tell(GetDetailsResult.apply(details));
        return effect.none();
    }

    @Override
    public Effect<UserEvent, State> onRegisterAccessToken(RegisterAccessToken register) {
        Optional<Token> existing = findExistingToken(register.getName());

        if (existing.isPresent()) {
            RegisteredAccessToken registered = RegisteredAccessToken.apply(existing.get());
            register.getReplyTo().tell(registered);

            return effect.none();
        } else {
            TokenDetails tokenDetails = TokenDetails.apply(
                register.getName(),
                UID.apply(),
                details.getId(),
                Instant.now(),
                register.getExecutor().getUserId(),
                Instant.now(),
                register.getExecutor().getUserId());

            Token token = Token.apply(UID.apply(), tokenDetails);

            RegisteredAccessToken registered = RegisteredAccessToken.apply(token);

            return effect
                .persist(registered)
                .thenRun(() -> register.getReplyTo().tell(registered));
        }
    }

    @Override
    public State onRegisteredAccessToken(RegisteredAccessToken registered) {
        this.details = this.details.withToken(registered.getToken());
        return this;
    }

    @Override
    public Effect<UserEvent, State> onRemoveAccessToken(RemoveAccessToken remove) {
        Optional<Token> existingToken = findExistingToken(remove.getName());

        RemovedAccessToken removed = RemovedAccessToken.apply(Instant.now(), remove.getExecutor().getUserId(), remove.getName());

        if (existingToken.isPresent()) {
            return effect
                .persist(removed)
                .thenRun(() -> remove.getReplyTo().tell(removed));
        } else {
            remove.getReplyTo().tell(removed);
            return effect.none();
        }
    }

    @Override
    public State onRemovedAccessToken(RemovedAccessToken removed) {
        this.details = this.details.withoutToken(removed.getToken());
        return this;
    }

    @Override
    public Effect<UserEvent, State> onRenewAccessTokenSecret(RenewAccessTokenSecret renew) {
        Optional<Token> existingToken = findExistingToken(renew.getName());

        if (existingToken.isPresent()) {
            TokenDetails newTokenDetails = existingToken
                .get()
                .getDetails()
                .withModified(Instant.now(), renew.getExecutor().getUserId());

            Token newToken = Token.apply(UID.apply(), newTokenDetails);
            RegisteredAccessToken registered = RegisteredAccessToken.apply(newToken);

            return effect
                .persist(registered)
                .thenRun(() -> renew.getReplyTo().tell(registered));
        } else {
            RegisterAccessToken register = RegisterAccessToken.apply(
                renew.getExecutor(),
                renew.getName(),
                renew.getReplyTo(),
                renew.getErrorTo());

            return onRegisterAccessToken(register);
        }
    }

    private Optional<Token> findExistingToken(ResourceName name) {
        return details
            .getAccessTokens()
            .stream()
            .filter(token -> token.getDetails().getName().equals(name))
            .findAny();
    }

}
