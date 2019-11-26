package maquette.controller.domain.api.commands.commands.projects;

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
import maquette.controller.domain.api.commands.commands.EAuthorizationType;
import maquette.controller.domain.api.commands.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.api.commands.views.SuccessVM;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChangeProjectOwnerCmd implements Command {

    private static final String PROJECT = "project";
    private static final String AUTHORIZATION = "authorization";
    private static final String TO = "to";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(AUTHORIZATION)
    private final EAuthorizationType authorization;

    @JsonProperty(TO)
    private final String to;

    @JsonCreator
    public static ChangeProjectOwnerCmd apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(AUTHORIZATION) EAuthorizationType authorization,
        @JsonProperty(TO) String to) {

        return new ChangeProjectOwnerCmd(project, authorization, to);
    }

    @Override
    public CompletionStage<ViewModel> run(User executor, CoreApplication app,
                                          OutputFormat outputFormat) {
        ObjectValidation.notNull().validate(project, PROJECT);
        ObjectValidation
            .validAuthorization(to)
            .and(ObjectValidation.notNull())
            .validate(authorization, AUTHORIZATION);

        return app
            .projects()
            .changeOwner(executor, project, authorization.asAuthorization(to))
            .thenApply(granted -> SuccessVM.apply());
    }

}
