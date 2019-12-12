package maquette.controller.domain.api.commands.projects;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.OutputFormat;
import maquette.controller.domain.api.ViewModel;
import maquette.controller.domain.api.Command;
import maquette.controller.domain.api.EAuthorizationType;
import maquette.controller.domain.api.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.api.views.SuccessVM;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectPrivilege;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class GrantProjectAccessCmd implements Command {

    private static final String PROJECT = "project";
    private static final String AUTHORIZATION = "authorization";
    private static final String PRIVILEGE = "privilege";
    private static final String TO = "to";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(AUTHORIZATION)
    private final EAuthorizationType authorization;

    @JsonProperty(PRIVILEGE)
    private final ProjectPrivilege privilege;

    @JsonProperty(TO)
    private final String to;

    @JsonCreator
    public static GrantProjectAccessCmd apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(AUTHORIZATION) EAuthorizationType authorization,
        @JsonProperty(PRIVILEGE) ProjectPrivilege privilege,
        @JsonProperty(TO) String to) {

        return new GrantProjectAccessCmd(project, authorization, privilege, to);
    }

    @Override
    public CompletionStage<ViewModel> run(User executor, CoreApplication app,
                                          OutputFormat outputFormat) {
        ObjectValidation.notNull().validate(project, PROJECT);
        ObjectValidation.notNull().validate(privilege, PRIVILEGE);
        ObjectValidation
            .validAuthorization(to)
            .and(ObjectValidation.notNull())
            .validate(authorization, AUTHORIZATION);

        return app
            .projects()
            .grantAccess(executor, project, privilege, authorization.asAuthorization(to))
            .thenApply(granted -> SuccessVM.apply());
    }

}
