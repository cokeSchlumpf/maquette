package maquette.controller.domain.api.commands.commands.projects;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.CommandResult;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.commands.Command;
import maquette.controller.domain.api.commands.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateProjectCmd implements Command {

    private static final String PROJECT = "project";
    private static final String DESCRIPTION = "description";
    private static final String IS_PRIVATE = "private";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(DESCRIPTION)
    private final Markdown description;

    @JsonProperty(IS_PRIVATE)
    private final boolean isPrivate;

    @JsonCreator
    public static CreateProjectCmd apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(DESCRIPTION) Markdown description,
        @JsonProperty(IS_PRIVATE) boolean isPrivate) {

        return new CreateProjectCmd(project, description, isPrivate);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app,
                                              OutputFormat outputFormat) {
        ObjectValidation.notNull().validate(project, PROJECT);

        return app
            .projects()
            .create(executor, project, description, isPrivate)
            .thenApply(info -> CommandResult.success());
    }

}
