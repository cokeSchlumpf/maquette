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
public final class GrantProjectAccessCmd implements Command {

    private static final String AUTHORIZATION = "authorization";
    private static final String PRIVILEGE = "privilege";
    private static final String PROJECT = "project";
    private static final String TO = "to";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(AUTHORIZATION)
    private final EAuthorizationType authorization;

    @JsonProperty(PRIVILEGE)
    private final NamespacePrivilege privilege;

    @JsonProperty(TO)
    private final String to;

    @JsonCreator
    public static GrantProjectAccessCmd apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(AUTHORIZATION) EAuthorizationType authorization,
        @JsonProperty(PRIVILEGE) NamespacePrivilege privilege,
        @JsonProperty(TO) String to) {

        return new GrantProjectAccessCmd(project, authorization, privilege, to);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(project, PROJECT);
        ObjectValidation.notNull().validate(privilege, PRIVILEGE);
        ObjectValidation
            .validAuthorization(to)
            .and(ObjectValidation.notNull())
            .validate(authorization, AUTHORIZATION);

        return app
            .projects()
            .grantAccess(executor, project, privilege, authorization.asAuthorization(to))
            .thenApply(granted -> CommandResult.success());
    }

}
