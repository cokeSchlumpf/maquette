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
import maquette.controller.domain.api.commands.commands.EAuthorizationType;
import maquette.controller.domain.api.commands.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectPrivilege;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RevokeProjectAccessCmd implements Command {

    private static final String AUTHORIZATION = "authorization";
    private static final String FROM = "from";
    private static final String PRIVILEGE = "privilege";
    private static final String PROJECT = "project";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(AUTHORIZATION)
    private final EAuthorizationType authorization;

    @JsonProperty(PRIVILEGE)
    private final ProjectPrivilege privilege;

    @JsonProperty(FROM)
    private final String from;

    @JsonCreator
    public static RevokeProjectAccessCmd apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(AUTHORIZATION) EAuthorizationType authorization,
        @JsonProperty(PRIVILEGE) ProjectPrivilege privilege,
        @JsonProperty(FROM) String from) {

        return new RevokeProjectAccessCmd(project, authorization, privilege, from);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app,
                                              OutputFormat outputFormat) {
        ObjectValidation.notNull().validate(project, PROJECT);
        ObjectValidation.notNull().validate(privilege, PRIVILEGE);
        ObjectValidation
            .validAuthorization(from)
            .and(ObjectValidation.notNull())
            .validate(authorization, AUTHORIZATION);

        return app
            .projects()
            .revokeAccess(executor, project, privilege, authorization.asAuthorization(from))
            .thenApply(granted -> CommandResult.success());
    }

}
