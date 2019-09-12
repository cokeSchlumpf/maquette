package maquette.controller.domain.entities.user;

import com.google.common.collect.Sets;

import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.cluster.sharding.typed.javadsl.EventSourcedEntity;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
import maquette.controller.domain.entities.user.protocol.UserEvent;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.entities.user.protocol.commands.RegisterAccessToken;
import maquette.controller.domain.entities.user.protocol.commands.RemoveAccessToken;
import maquette.controller.domain.entities.user.protocol.commands.RenewAccessTokenSecret;
import maquette.controller.domain.entities.user.protocol.events.RegisteredAccessToken;
import maquette.controller.domain.entities.user.protocol.events.RemovedAccessToken;
import maquette.controller.domain.entities.user.protocol.queries.GetDetails;
import maquette.controller.domain.entities.user.state.ActiveUser;
import maquette.controller.domain.entities.user.state.State;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.util.databind.ObjectMapperFactory;
import maquette.controller.domain.values.iam.UserDetails;
import maquette.controller.domain.values.iam.UserId;

public class User extends EventSourcedEntity<UserMessage, UserEvent, State> {

    public static EntityTypeKey<UserMessage> ENTITY_KEY = EntityTypeKey.create(UserMessage.class, "user");

    private final UserId userId;

    public User(String entityId, UserId userId) {
        super(ENTITY_KEY, entityId);
        this.userId = userId;
    }

    public static String createEntityId(UserId userId) {
        return Operators.suppressExceptions(() -> ObjectMapperFactory.apply().create().writeValueAsString(userId));
    }

    public static UserId fromEntityId(String entityId) {
        return Operators.suppressExceptions(() -> ObjectMapperFactory.apply().create().readValue(entityId, UserId.class));
    }

    public static EventSourcedEntity<UserMessage, UserEvent, State> create(UserId userId) {
        String entityId = createEntityId(userId);
        return new User(entityId, userId);
    }

    @Override
    public State emptyState() {
        return ActiveUser.apply(Effect(), UserDetails.apply(userId, Sets.newHashSet()));
    }

    @Override
    public CommandHandler<UserMessage, UserEvent, State> commandHandler() {
        return newCommandHandlerBuilder()
            .forAnyState()
            .onCommand(RegisterAccessToken.class, State::onRegisterAccessToken)
            .onCommand(RemoveAccessToken.class, State::onRemoveAccessToken)
            .onCommand(RenewAccessTokenSecret.class, State::onRenewAccessTokenSecret)
            .onCommand(GetDetails.class, State::onGetDetails)
            .build();
    }

    @Override
    public EventHandler<State, UserEvent> eventHandler() {
        return newEventHandlerBuilder()
            .forAnyState()
            .onEvent(RegisteredAccessToken.class, State::onRegisteredAccessToken)
            .onEvent(RemovedAccessToken.class, State::onRemovedAccessToken)
            .build();
    }

}
