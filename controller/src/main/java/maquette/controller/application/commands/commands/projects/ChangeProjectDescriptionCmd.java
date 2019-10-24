package maquette.controller.application.commands.commands.projects;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.application.commands.CommandResult;
import maquette.controller.application.commands.OutputFormat;
import maquette.controller.application.commands.commands.Command;
import maquette.controller.application.commands.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeProjectDescriptionCmd implements Command {

    private static final String PROJECT = "project";
    private static final String DESCRIPTION = "description";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(DESCRIPTION)
    private final Markdown description;

    @JsonCreator
    public static ChangeProjectDescriptionCmd apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(DESCRIPTION) Markdown description) {

        return new ChangeProjectDescriptionCmd(project, description);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app,
                                              OutputFormat outputFormat) {
        ObjectValidation.notNull().validate(project, PROJECT);

        return app
            .projects()
            .changeDescription(executor, project, description)
            .thenApply(info -> CommandResult.success());
    }

}