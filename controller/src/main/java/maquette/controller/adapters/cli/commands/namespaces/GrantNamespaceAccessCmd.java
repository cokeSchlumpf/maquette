package maquette.controller.adapters.cli.commands.namespaces;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.adapters.cli.commands.EAuthorizationType;
import maquette.controller.adapters.cli.commands.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespacePrivilege;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class GrantNamespaceAccessCmd implements Command {

    private final String namespace;

    private final EAuthorizationType authorization;

    private final NamespacePrivilege privilege;

    private final String to;

    @JsonCreator
    public static GrantNamespaceAccessCmd apply(
        @JsonProperty("namespace") String namespace,
        @JsonProperty("authorization") EAuthorizationType authorization,
        @JsonProperty("privilege") NamespacePrivilege privilege,
        @JsonProperty("to") String to) {

        return new GrantNamespaceAccessCmd(namespace, authorization, privilege, to);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(privilege, "privilege");
        ObjectValidation
            .validAuthorization(to)
            .and(ObjectValidation.notNull())
            .validate(authorization, "authorization");

        ResourceName resource;

        if (namespace == null) {
            resource = ResourceName.apply(executor.getUserId().getId());
        } else {
            resource = ResourceName.apply(namespace);
        }

        return app
            .namespaces()
            .grantNamespaceAccess(executor, resource, privilege, authorization.asAuthorization(to))
            .thenApply(granted -> CommandResult.success());
    }

}
