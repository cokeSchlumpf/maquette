package maquette.controller.application.commands.views;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.application.commands.ViewModel;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserVM implements ViewModel {

    private static final String NAME = "name";
    private static final String ROLES = "roles";
    private static final String NOTIFICATIONS = "notifications";

    @JsonProperty(NAME)
    private final String name;

    @JsonProperty(ROLES)
    private final Set<String> roles;

    @JsonProperty(NOTIFICATIONS)
    private final int notifications;

    @JsonCreator
    public static UserVM apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(ROLES) Iterable<String> roles,
        @JsonProperty(NOTIFICATIONS) int notifications) {

        return new UserVM(name, ImmutableSet.copyOf(roles), notifications);
    }

}
