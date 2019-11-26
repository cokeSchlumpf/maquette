package maquette.controller.domain.values.notification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserId;
import maquette.controller.domain.values.notification.actions.NotificationAction;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Notification {

    private static final String ID = "id";
    private static final String SENT = "sent";
    private static final String TO = "to";
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String ACTIONS = "actions";
    private static final String READ = "read";

    @JsonProperty(ID)
    private final UID id;

    @JsonProperty(SENT)
    private final Instant sent;

    @JsonProperty(TO)
    private final Authorization to;

    @JsonProperty(MESSAGE)
    private final Markdown message;

    @JsonProperty(ACTIONS)
    private final List<NotificationAction> actions;

    @JsonProperty(READ)
    private final Set<NotificationRead> read;

    @JsonCreator
    public static Notification apply(
        @JsonProperty(ID) UID id,
        @JsonProperty(SENT) Instant sent,
        @JsonProperty(TO) Authorization to,
        @JsonProperty(MESSAGE) Markdown message,
        @JsonProperty(ACTIONS) List<NotificationAction> actions,
        @JsonProperty(READ) Set<NotificationRead> read) {

        return new Notification(id, sent, to, message, ImmutableList.copyOf(actions), ImmutableSet.copyOf(read));
    }

    public static Notification apply(UID id, Instant sent, Authorization to, Markdown message) {
        return new Notification(id, sent, to, message, Lists.newArrayList(), Sets.newHashSet());
    }

    public static Notification apply(UID id, Instant sent, Authorization to, Markdown message, List<NotificationAction> actions) {
        return new Notification(id, sent, to, message, actions, Sets.newHashSet());
    }

    public Optional<NotificationRead> getRead(User fromUser) {
        return getRead(fromUser.getUserId());
    }

    public Optional<NotificationRead> getRead(UserId fromUser) {
        return getRead()
            .stream()
            .filter(r -> r.getBy().equals(fromUser))
            .findAny();
    }

    public Notification withMarkedAsRead(UserId by, Instant at) {
        if (getRead(by).isPresent()) {
            return this;
        } else {
            Set<NotificationRead> read = Sets.newHashSet(this.read);
            read.add(NotificationRead.apply(by, at));

            return apply(id, sent, to, message, actions, read);
        }
    }

}
