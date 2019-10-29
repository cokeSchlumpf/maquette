package maquette.controller.domain.api.commands.commands.users;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Lists;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.CommandResult;
import maquette.controller.domain.api.commands.DataTable;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.commands.Command;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.notification.Notification;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetNotificationsCmd implements Command {

    @JsonCreator
    public static GetNotificationsCmd apply() {
        return new GetNotificationsCmd();
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app,
                                              OutputFormat outputFormat) {
        return app
            .users()
            .getNotifications(executor)
            .thenApply(notifications -> Lists.newArrayList(notifications.iterator()))
            .thenApply(notifications -> {
                List<Notification> sorted = notifications
                    .stream()
                    .sorted(Comparator.comparing(Notification::getSent).reversed())
                    .collect(Collectors.toList());

                List<String> messages = Lists.newArrayList();

                for (Notification n : sorted) {
                    StringBuilder sb = new StringBuilder();
                    DataTable header = DataTable
                        .apply("key", "value")
                        .withRow("id", n.getId())
                        .withRow("received", n.getSent())
                        .withRow("read", n.getRead().size() > 0);

                    sb.append(header.toAscii(true, true));
                    sb.append("\n");
                    sb.append(n.getMessage().asASCIIString());

                    messages.add(sb.toString());
                }

                return CommandResult.success(String.join("\n\n------------\n\n", messages));
            });
    }
}
