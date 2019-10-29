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
