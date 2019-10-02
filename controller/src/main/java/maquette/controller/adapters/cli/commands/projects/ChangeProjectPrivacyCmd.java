package maquette.controller.adapters.cli.commands.projects;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.adapters.cli.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeProjectPrivacyCmd implements Command {

    private static final String PROJECT = "project";
    private static final String IS_PRIVATE = "is-private";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(IS_PRIVATE)
    private final boolean isPrivate;

    @JsonCreator
    public static ChangeProjectPrivacyCmd apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(IS_PRIVATE) boolean isPrivate) {

        return new ChangeProjectPrivacyCmd(project, isPrivate);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(project, PROJECT);

        return app
            .projects()
            .changePrivacy(executor, project, isPrivate)
            .thenApply(info -> CommandResult.success());
    }

}
