package maquette.controller.domain.api.commands.views;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.CommandResult;
import maquette.controller.domain.api.commands.DataTable;
import maquette.controller.domain.api.commands.ViewModel;
import maquette.controller.domain.values.dataset.DatasetAccessRequestLink;
import maquette.controller.domain.values.iam.PersonalUserProfile;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.notification.Notification;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserVM implements ViewModel {

    private static final String NAME = "name";
    private static final String REQUESTS = "requests";
    private static final String ROLES = "roles";
    private static final String NOTIFICATIONS = "notifications";

    @JsonProperty(NAME)
    private final String name;

    @JsonProperty(ROLES)
    private final Set<String> roles;

    @JsonProperty(NOTIFICATIONS)
    private final int notifications;

    @JsonProperty(REQUESTS)
    private final List<DatasetAccessRequestLink> requests;

    @JsonCreator
    public static UserVM apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(ROLES) Iterable<String> roles,
        @JsonProperty(REQUESTS) List<DatasetAccessRequestLink> requests,
        @JsonProperty(NOTIFICATIONS) int notifications) {

        return new UserVM(name, ImmutableSet.copyOf(roles), notifications, ImmutableList.copyOf(requests));
    }

    public static UserVM apply(User executor, Set<Notification> notifications, PersonalUserProfile profile) {
        return apply(executor.getUserId().getId(), executor.getRoles(),
                     Lists.newArrayList(profile.getDatasetAccessRequests()), notifications.size());
    }

    @Override
    public CommandResult toCommandResult(ObjectMapper om) {
        DataTable dt = DataTable
            .apply("KEY", "VALUE")
            .withRow("name", name)
            .withRow("roles", String.join(", ", roles))
            .withRow("notifications", notifications);

        DataTable accessRequests = DataTable.apply("ID", "DATASET");
        for (DatasetAccessRequestLink link : requests) {
            accessRequests = accessRequests.withRow(link.getId(), link.getDataset());
        }

        String sb = ""
                    + "PROPERTIES\n"
                    + "----------\n"
                    + dt.toAscii(false, true)
                    + "\n\n"
                    + "DATASET ACCESS REQUESTS\n"
                    + "-----------------------\n"
                    + accessRequests.toAscii();

        return CommandResult.success(sb, accessRequests);
    }
}
