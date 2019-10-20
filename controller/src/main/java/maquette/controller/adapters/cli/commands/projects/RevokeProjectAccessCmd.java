package maquette.controller.adapters.cli.commands.projects;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.adapters.cli.commands.EAuthorizationType;
import maquette.controller.adapters.cli.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.NamespacePrivilege;

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
    private final NamespacePrivilege privilege;

    @JsonProperty(FROM)
    private final String from;

    @JsonCreator
    public static RevokeProjectAccessCmd apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(AUTHORIZATION) EAuthorizationType authorization,
        @JsonProperty(PRIVILEGE) NamespacePrivilege privilege,
        @JsonProperty(FROM) String from) {

        return new RevokeProjectAccessCmd(project, authorization, privilege, from);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(project, PROJECT);
        ObjectValidation.notNull().validate(privilege, PRIVILEGE);
        ObjectValidation
            .validAuthorization(from)
            .and(ObjectValidation.notNull())
            .validate(authorization, AUTHORIZATION);

        return app
            .projects()
            .revokeNamespaceAccess(executor, project, privilege, authorization.asAuthorization(from))
            .thenApply(granted -> CommandResult.success());
    }

}
