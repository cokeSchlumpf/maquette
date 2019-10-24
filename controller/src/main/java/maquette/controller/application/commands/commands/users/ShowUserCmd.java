package maquette.controller.application.commands.commands.users;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.application.commands.CommandResult;
import maquette.controller.application.commands.DataTable;
import maquette.controller.application.commands.OutputFormat;
import maquette.controller.application.commands.commands.Command;
import maquette.controller.application.commands.views.UserVM;
import maquette.controller.domain.CoreApplication;
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
        return app
            .users()
            .getNotifications(executor)
            .thenApply(notifications -> {
                DataTable dt = DataTable
                    .apply("KEY", "VALUE")
                    .withRow("name", executor.getDisplayName())
                    .withRow("roles", String.join(", ", executor.getRoles()))
                    .withRow("notifications", notifications.size());

                UserVM view = UserVM.apply(executor.getDisplayName(), executor.getRoles(), notifications.size());

                return CommandResult
                    .success(dt.toAscii(false, true), dt)
                    .withView(view);
            });
    }
}
