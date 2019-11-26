package maquette.controller.domain.api.commands.commands.users;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.CommandResult;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.ViewModel;
import maquette.controller.domain.api.commands.commands.Command;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.api.commands.views.SimpleMessageVM;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisterTokenCmd implements Command {

    private static final String FOR = "for";
    private static final String NAME = "name";

    @JsonProperty(FOR)
    private final String forUser;

    @JsonProperty(NAME)
    private final String name;

    @JsonCreator
    public static RegisterTokenCmd apply(
        @JsonProperty(FOR) String forUser,
        @JsonProperty(NAME) String name) {

        return new RegisterTokenCmd(forUser, name);
    }


    @Override
    public CompletionStage<ViewModel> run(User executor, CoreApplication app,
                                          OutputFormat outputFormat) {
        return app
            .users()
            .registerToken(executor, UserId.apply(executor, forUser), ResourceName.apply(name))
            .thenApply(token -> {
                String out =
                    String.format("Registered token '%s' with secret '%s'", token.getDetails().getName(), token.getSecret());

                return SimpleMessageVM.apply(out);
            });
    }

}
