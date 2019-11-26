package maquette.controller.domain.api.commands.commands.users;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.ViewModel;
import maquette.controller.domain.api.commands.commands.Command;
import maquette.controller.domain.api.commands.views.SimpleMessageVM;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteTokenCmd implements Command {

    private static final String FOR = "for";
    private static final String NAME = "name";

    @JsonProperty(FOR)
    private final String forUser;

    @JsonProperty(NAME)
    private final String name;

    @JsonCreator
    public static DeleteTokenCmd apply(
        @JsonProperty(FOR) String forUser,
        @JsonProperty(NAME) String name) {

        return new DeleteTokenCmd(forUser, name);
    }


    @Override
    public CompletionStage<ViewModel> run(User executor, CoreApplication app,
                                          OutputFormat outputFormat) {
        return app
            .users()
            .deleteAccessToken(executor, UserId.apply(executor, forUser), ResourceName.apply(name))
            .thenApply(token -> {
                String out = String.format("Deleted access token '%s'", name);
                return SimpleMessageVM.apply(out);
            });
    }

}
