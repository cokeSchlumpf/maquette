package maquette.controller.adapters.cli.commands.namespaces;

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
public final class RevokeNamespaceAccessCmd implements Command {

    private final String namespace;

    private final EAuthorizationType authorization;

    private final NamespacePrivilege privilege;

    private final String from;

    @JsonCreator
    public static RevokeNamespaceAccessCmd apply(
        @JsonProperty("namespace") String namespace,
        @JsonProperty("authorization") EAuthorizationType authorization,
        @JsonProperty("privilege") NamespacePrivilege privilege,
        @JsonProperty("from") String from) {

        return new RevokeNamespaceAccessCmd(namespace, authorization, privilege, from);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(privilege, "privilege");
        ObjectValidation
            .validAuthorization(from)
            .and(ObjectValidation.notNull())
            .validate(authorization, "authorization");

        ResourceName resource = ResourceName.apply(executor, namespace);

        return app
            .namespaces()
            .revokeNamespaceAccess(executor, resource, privilege, authorization.asAuthorization(from))
            .thenApply(granted -> CommandResult.success());
    }

}
