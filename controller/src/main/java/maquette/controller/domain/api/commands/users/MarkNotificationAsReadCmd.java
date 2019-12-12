package maquette.controller.domain.api.commands.users;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.OutputFormat;
import maquette.controller.domain.api.ViewModel;
import maquette.controller.domain.api.Command;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.api.views.SuccessVM;
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
    public CompletionStage<ViewModel> run(User executor, CoreApplication app,
                                          OutputFormat outputFormat) {
        return app
            .users()
            .markNotificationAsRead(executor, id)
            .thenApply(n -> SuccessVM.apply());
    }

}
