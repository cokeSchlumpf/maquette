package maquette.controller.adapters.cli.commands;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.RoleAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserAuthorization;
import maquette.controller.domain.values.iam.WildcardAuthorization;
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
        ResourceName resource;

        if (privilege == null) {
            return CompletableFuture.completedFuture(CommandResult.error("A privilege must be specified"));
        } else if (authorization == null) {
            return CompletableFuture.completedFuture(CommandResult.error("A valid authorization must be specified"));
        } else if (authorization.equals(EAuthorizationType.ROLE) && to == null) {
            return CompletableFuture.completedFuture(CommandResult.error("A role must be specified"));
        } else if (authorization.equals(EAuthorizationType.USER) && to == null) {
            return CompletableFuture.completedFuture(CommandResult.error("A user must be specified"));
        }

        if (namespace == null) {
            resource = ResourceName.apply(executor.getUserId().getId());
        } else {
            resource = ResourceName.apply(namespace);
        }

        return app
            .namespaces()
            .grantNamespaceAccess(executor, resource, privilege, getAuthorization())
            .thenApply(granted -> CommandResult.success());
    }

    private Authorization getAuthorization() {
        switch (authorization) {
            case USER:
                return UserAuthorization.apply(to);
            case ROLE:
                return RoleAuthorization.apply(to);
            case WILDCARD:
                return WildcardAuthorization.apply();
            default:
                throw new IllegalArgumentException("Unknown authorization type");
        }
    }

}
