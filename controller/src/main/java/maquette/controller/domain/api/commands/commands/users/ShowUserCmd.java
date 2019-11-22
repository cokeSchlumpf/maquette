package maquette.controller.domain.api.commands.commands.users;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.CommandResult;
import maquette.controller.domain.api.commands.DataTable;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.commands.Command;
import maquette.controller.domain.api.commands.views.UserVM;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.dataset.DatasetAccessRequestLink;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShowUserCmd implements Command {

    @JsonCreator
    public static ShowUserCmd apply() {
        return new ShowUserCmd();
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app,
                                              OutputFormat outputFormat) {

        return Operators
            .compose(
                app.users().getNotifications(executor),
                app.users().getPersonalUserProfile(executor),
                (notifications, profile) -> {
                    DataTable dt = DataTable
                        .apply("KEY", "VALUE")
                        .withRow("name", executor.getDisplayName())
                        .withRow("roles", String.join(", ", executor.getRoles()))
                        .withRow("notifications", notifications.size());

                    DataTable accessRequests = DataTable.apply("ID", "DATASET");
                    for (DatasetAccessRequestLink link : profile.getDatasetAccessRequests()) {
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

                    return CommandResult
                        .success(sb, dt, accessRequests)
                        .withView(UserVM.apply(executor, notifications, profile));
                });
    }
}
