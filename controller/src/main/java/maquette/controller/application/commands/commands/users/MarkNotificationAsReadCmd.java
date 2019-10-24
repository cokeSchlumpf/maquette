package maquette.controller.application.commands.commands.users;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.application.commands.CommandResult;
import maquette.controller.application.commands.commands.Command;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MarkNotificationAsReadCmd implements Command {

    private static final String ID = "id";

    @JsonProperty(ID)
    private final UID id;

    @JsonCreator
    public static MarkNotificationAsReadCmd apply(@JsonProperty(ID) UID id) {
        return new MarkNotificationAsReadCmd(id);
    }


    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        return app
            .users()
            .markNotificationAsRead(executor, id)
            .thenApply(n -> CommandResult.success());
    }

}
