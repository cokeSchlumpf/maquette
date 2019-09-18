package maquette.controller.adapters.cli.commands.datasets;

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
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RevokeDatasetAccessCmd implements Command {

    private final String namespace;

    private final String dataset;

    private final EAuthorizationType authorization;

    private final DatasetPrivilege privilege;

    private final String from;

    @JsonCreator
    public static RevokeDatasetAccessCmd apply(
        @JsonProperty("namespace") String namespace,
        @JsonProperty("dataset") String dataset,
        @JsonProperty("authorization") EAuthorizationType authorization,
        @JsonProperty("privilege") DatasetPrivilege privilege,
        @JsonProperty("from") String from) {

        return new RevokeDatasetAccessCmd(namespace, dataset, authorization, privilege, from);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(privilege, "privilege");
        ObjectValidation.notNull().validate(dataset, "dataset");
        ObjectValidation
            .validAuthorization(from)
            .and(ObjectValidation.notNull())
            .validate(authorization, "authorization");

        ResourcePath resource = ResourcePath.apply(executor, namespace, dataset);

        return app
            .datasets()
            .revokeDatasetAccess(executor, resource, privilege, authorization.asAuthorization(from))
            .thenApply(granted -> CommandResult.success());
    }

}
