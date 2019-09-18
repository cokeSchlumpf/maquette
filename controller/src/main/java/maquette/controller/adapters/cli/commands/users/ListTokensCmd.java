package maquette.controller.adapters.cli.commands.users;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.persistence.typed.javadsl.CommandHandlerWithReplyBuilderByState$;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.DataTable;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.iam.TokenDetails;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ListTokensCmd implements Command {

    private static final String FOR_USER = "for-user";

    @JsonProperty(FOR_USER)
    private final String forUser;

    @JsonCreator
    public static ListTokensCmd apply(
        @JsonProperty(FOR_USER) String forUser) {

        return new ListTokensCmd(forUser);
    }


    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        return app
            .users()
            .getTokens(executor, UserId.apply(executor, forUser))
            .thenApply(tokens -> {
                DataTable dt = DataTable.apply("name", "id", "created", "created by", "modified", "modified by");

                for (TokenDetails token : tokens) {
                    dt = dt.withRow(
                        token.getName(), token.getId(), token.getCreated(),
                        token.getCreatedBy(), token.getModified(), token.getModifiedBy());
                }

                return CommandResult.success(dt.toAscii(), dt);
            });
    }

}
