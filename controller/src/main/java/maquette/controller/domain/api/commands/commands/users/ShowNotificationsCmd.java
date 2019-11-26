package maquette.controller.domain.api.commands.commands.users;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.api.commands.DataTable;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.ViewModel;
import maquette.controller.domain.api.commands.commands.Command;
import maquette.controller.domain.api.commands.views.SimpleMessageVM;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.notification.NotificationRead;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShowNotificationsCmd implements Command {


    @JsonCreator
    public static ShowNotificationsCmd apply() {
        return new ShowNotificationsCmd();
    }


    @Override
    public CompletionStage<ViewModel> run(User executor, CoreApplication app,
                                          OutputFormat outputFormat) {
        return app
            .users()
            .getNotifications(executor)
            .thenApply(notifications -> {
                String notificationsPrinted = notifications
                    .stream()
                    .map(notification -> {
                        Optional<NotificationRead> read = notification.getRead(executor);

                        DataTable header = DataTable
                            .apply("key", "value")
                            .withRow("to", notification.getTo())
                            .withRow("received", notification.getSent())
                            .withRow("read", read.map(NotificationRead::getAt));

                        String n =  header.toAscii(false, true) + "\n" + notification.getMessage().asASCIIString();

                        if (notification.getActions().size() > 0) {
                            String actions = notification
                                .getActions()
                                .stream()
                                .map(action -> {
                                    String command = action.toCommand().map(c -> String.format("\n     Command: `mq %s`", c)).orElse("");
                                    return "   - " + action.toMessage() + command;
                                })
                                .collect(Collectors.joining("\n"));

                            n = n + "\n\nActions:\n" + actions;
                        }

                        return n;
                    })
                    .collect(Collectors.joining("\n\n---\n\n"));

                String out;

                if (notifications.size() > 0) {
                    out = notificationsPrinted;
                } else {
                    out = "No notifications available";
                }

                return SimpleMessageVM.apply(out);
            });
    }

}
