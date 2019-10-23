package maquette.controller.domain.entities.notifcation;

import java.time.Instant;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.typed.SingletonActor;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EventHandler;
import akka.persistence.typed.javadsl.EventSourcedBehavior;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.controller.domain.entities.notifcation.protocol.NotificationsEvent;
import maquette.controller.domain.entities.notifcation.protocol.NotificationsMessage;
import maquette.controller.domain.entities.notifcation.protocol.commands.CreateNotification;
import maquette.controller.domain.entities.notifcation.protocol.commands.MarkNotificationAsRead;
import maquette.controller.domain.entities.notifcation.protocol.events.CreatedNotification;
import maquette.controller.domain.entities.notifcation.protocol.events.MarkedNotificationAsRead;
import maquette.controller.domain.entities.notifcation.protocol.queries.GetNotification;
import maquette.controller.domain.entities.notifcation.protocol.queries.GetNotifications;
import maquette.controller.domain.entities.notifcation.protocol.results.GetNotificationResult;
import maquette.controller.domain.entities.notifcation.protocol.results.GetNotificationsResult;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.notification.Notification;
import maquette.controller.domain.values.notification.NotificationDoesNotExistError;

public final class Notifications extends EventSourcedBehavior<NotificationsMessage, NotificationsEvent, Notifications.State> {

    private static final String PERSISTENCE_ID = "notifications";

    private Notifications() {
        super(PersistenceId.apply(PERSISTENCE_ID));
    }

    public static SingletonActor<NotificationsMessage> create() {
        Behavior<NotificationsMessage> behavior = Behaviors.setup(actor -> new Notifications());
        return SingletonActor.apply(behavior, PERSISTENCE_ID);
    }

    @Override
    public State emptyState() {
        return State.apply();
    }

    @Override
    public CommandHandler<NotificationsMessage, NotificationsEvent, State> commandHandler() {
        return newCommandHandlerBuilder()
            .forAnyState()
            .onCommand(CreateNotification.class, this::onCreateNotification)
            .onCommand(MarkNotificationAsRead.class, this::onMarkNotificationAsRead)
            .onCommand(GetNotification.class, this::onGetNotification)
            .onCommand(GetNotifications.class, this::onGetNotifications)
            .build();
    }

    @Override
    public EventHandler<State, NotificationsEvent> eventHandler() {
        return newEventHandlerBuilder()
            .forAnyState()
            .onEvent(CreatedNotification.class, this::onCreatedNotification)
            .onEvent(MarkedNotificationAsRead.class, this::onMarkedNotificationAsRead)
            .build();
    }

    private Effect<NotificationsEvent, State> onCreateNotification(CreateNotification create) {
        UID id = UID.apply(8);

        Notification notification = Notification.apply(
            id, Instant.now(), create.getTo(), create.getTitle(), create.getMessage(), create.getActions());

        CreatedNotification created = CreatedNotification.apply(Instant.now(), notification);

        return Effect()
            .persist(created)
            .thenRun(() -> create.getReplyTo().tell(created));
    }

    private State onCreatedNotification(State state, CreatedNotification created) {
        state.getNotifications().put(created.getNotification().getId(), created.getNotification());
        return state;
    }

    private Effect<NotificationsEvent, State> onMarkNotificationAsRead(State state, MarkNotificationAsRead mark) {
        if (state.getNotifications().containsKey(mark.getNotification())) {
            MarkedNotificationAsRead marked = MarkedNotificationAsRead.apply(
                mark.getExecutor().getUserId(), Instant.now(), mark.getNotification());

            return Effect()
                .persist(marked)
                .thenRun(() -> mark.getReplyTo().tell(marked));
        } else {
            mark.getErrorTo().tell(NotificationDoesNotExistError.apply(mark.getNotification()));
            return Effect().none();
        }
    }

    private State onMarkedNotificationAsRead(State state, MarkedNotificationAsRead marked) {
        Notification notification =
            state.getNotifications().get(marked.getNotification()).withMarkedAsRead(marked.getReadBy(), marked.getReadAt());

        state.getNotifications().put(marked.getNotification(), notification);

        return state;
    }

    private Effect<NotificationsEvent, State> onGetNotification(State state, GetNotification get) {
        if (state.getNotifications().containsKey(get.getId())) {
            GetNotificationResult result = GetNotificationResult.apply(state.getNotifications().get(get.getId()));
            get.getReplyTo().tell(result);
        } else {
            get.getErrorTo().tell(NotificationDoesNotExistError.apply(get.getId()));
        }
        return Effect().none();
    }

    private Effect<NotificationsEvent, State> onGetNotifications(State state, GetNotifications get) {
        get.getReplyTo().tell(GetNotificationsResult.apply(Sets.newHashSet(state.getNotifications().values())));
        return Effect().none();
    }

    @Getter
    @AllArgsConstructor(staticName = "apply")
    public static class State {

        private Map<UID, Notification> notifications;

        public static State apply() {
            return apply(Maps.newHashMap());
        }

    }

}
