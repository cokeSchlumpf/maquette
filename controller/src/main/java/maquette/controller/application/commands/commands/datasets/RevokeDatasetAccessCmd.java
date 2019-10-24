package maquette.controller.application.commands.commands.datasets;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.application.commands.CommandResult;
import maquette.controller.application.commands.OutputFormat;
import maquette.controller.application.commands.commands.Command;
import maquette.controller.application.commands.commands.EAuthorizationType;
import maquette.controller.application.commands.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RevokeDatasetAccessCmd implements Command {

    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String AUTHORIZATION = "authorization";
    private static final String PRIVILEGE = "privilege";
    private static final String FROM = "from";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(DATASET)
    private final ResourceName dataset;

    @JsonProperty(AUTHORIZATION)
    private final EAuthorizationType authorization;

    @JsonProperty(PRIVILEGE)
    private final DatasetPrivilege privilege;

    @JsonProperty(FROM)
    private final String from;

    @JsonCreator
    public static RevokeDatasetAccessCmd apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(DATASET) ResourceName dataset,
        @JsonProperty(AUTHORIZATION) EAuthorizationType authorization,
        @JsonProperty(PRIVILEGE) DatasetPrivilege privilege,
        @JsonProperty(FROM) String from) {

        return new RevokeDatasetAccessCmd(project, dataset, authorization, privilege, from);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app,
                                              OutputFormat outputFormat) {
        ObjectValidation.notNull().validate(privilege, PRIVILEGE);
        ObjectValidation.notNull().validate(dataset, DATASET);
        ObjectValidation.notNull().validate(from, FROM);
        ObjectValidation
            .validAuthorization(from)
            .and(ObjectValidation.notNull())
            .validate(authorization, AUTHORIZATION);

        ResourcePath resource = ResourcePath.apply(executor, project, dataset);

        return app
            .datasets()
            .revokeDatasetAccess(executor, resource, privilege, authorization.asAuthorization(from))
            .thenApply(granted -> CommandResult.success());
    }

}
