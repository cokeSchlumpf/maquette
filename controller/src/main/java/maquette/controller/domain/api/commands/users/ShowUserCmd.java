package maquette.controller.domain.api.commands.users;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.api.OutputFormat;
import maquette.controller.domain.api.ViewModel;
import maquette.controller.domain.api.Command;
import maquette.controller.domain.api.views.UserVM;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShowUserCmd implements Command {

    @JsonCreator
    public static ShowUserCmd apply() {
        return new ShowUserCmd();
    }

    @Override
    public CompletionStage<ViewModel> run(
        User executor,
        CoreApplication app,
        OutputFormat outputFormat) {

        return Operators
            .compose(
                app.users().getNotifications(executor),
                app.users().getPersonalUserProfile(executor),
                (notifications, profile) -> UserVM.apply(executor, notifications, profile));
    }
}
