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
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class GrantDatasetAccessCmd implements Command {

    private static final String NAMESPACE = "namespace";
    private static final String DATASET = "dataset";
    private static final String AUTHORIZATION = "authorization";
    private static final String PRIVILEGE = "privilege";
    private static final String TO = "to";

    @JsonProperty(NAMESPACE)
    private final ResourceName namespace;

    @JsonProperty(DATASET)
    private final ResourceName dataset;

    @JsonProperty(AUTHORIZATION)
    private final EAuthorizationType authorization;

    @JsonProperty(PRIVILEGE)
    private final DatasetPrivilege privilege;

    @JsonProperty(TO)
    private final String to;

    @JsonCreator
    public static GrantDatasetAccessCmd apply(
        @JsonProperty(NAMESPACE) ResourceName namespace,
        @JsonProperty(DATASET) ResourceName dataset,
        @JsonProperty(AUTHORIZATION) EAuthorizationType authorization,
        @JsonProperty(PRIVILEGE) DatasetPrivilege privilege,
        @JsonProperty(TO) String to) {

        return new GrantDatasetAccessCmd(namespace, dataset, authorization, privilege, to);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(privilege, PRIVILEGE);
        ObjectValidation.notNull().validate(dataset, DATASET);
        ObjectValidation.notNull().validate(to, TO);
        ObjectValidation
            .validAuthorization(to)
            .and(ObjectValidation.notNull())
            .validate(authorization, AUTHORIZATION);

        ResourcePath resource = ResourcePath.apply(executor, namespace, dataset);

        return app
            .datasets()
            .grantDatasetAccess(executor, resource, privilege, authorization.asAuthorization(to))
            .thenApply(granted -> CommandResult.success());
    }

}
