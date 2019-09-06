package maquette.controller.application.resources;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import maquette.controller.application.model.NamespaceAccessRequest;
import maquette.controller.application.util.ContextUtils;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespaceDetails;
import maquette.controller.domain.values.namespace.NamespaceInfo;

@AllArgsConstructor
@RestController("Namespaces")
@RequestMapping("api/v1/namespaces")
public class NamespacesResource {

    private final CoreApplication core;

    @RequestMapping(
        path = "{name}/settings/owner",
        method = RequestMethod.POST,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "Change the owner of a namespace")
    public CompletionStage<NamespaceInfo> changeOwner(
        @RequestParam("name") String name,
        @RequestBody Authorization owner,
        ServerWebExchange exchange) {

        User user = ContextUtils.getUser(exchange);
        ResourceName resourceName = ResourceName.apply(name);

        return core
            .namespaces()
            .changeOwner(user, resourceName, owner);
    }

    @RequestMapping(
        path = "{name}",
        method = RequestMethod.PUT,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "Creates a new namespace")
    public CompletionStage<NamespaceInfo> create(
        @RequestParam("name") String name,
        ServerWebExchange exchange) {

        ResourceName resourceName = ResourceName.apply(name);
        return core
            .namespaces()
            .createNamespace(ContextUtils.getUser(exchange), resourceName);
    }

    @RequestMapping(
        path = "{name}",
        method = RequestMethod.DELETE,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "Delete an existing Namespace")
    public CompletionStage<Void> delete(
        @RequestParam("name") String name,
        ServerWebExchange exchange) {

        User user = ContextUtils.getUser(exchange);
        ResourceName resourceName = ResourceName.apply(name);

        return core
            .namespaces()
            .deleteNamespace(user, resourceName)
            .thenRun(() -> {});
    }

    @RequestMapping(
        path = "{name}",
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "Grant access to a repository")
    public CompletionStage<NamespaceDetails> get(
        @RequestParam("name") String name,
        ServerWebExchange exchange) {

        User user = ContextUtils.getUser(exchange);
        ResourceName resourceName = ResourceName.apply(name);

        return core
            .namespaces()
            .getNamespaceDetails(user, resourceName);
    }

    @RequestMapping(
        path = "{name}/settings/access",
        method = RequestMethod.POST,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "Grant access to a repository")
    public CompletionStage<GrantedAuthorization> grant(
        @RequestParam("name") String name,
        @RequestBody NamespaceAccessRequest request,
        ServerWebExchange exchange) {

        User user = ContextUtils.getUser(exchange);
        ResourceName resourceName = ResourceName.apply(name);

        return core
            .namespaces()
            .grantNamespaceAccess(user, resourceName, request.getPrivilege(), request.getAuthorization());
    }

    @RequestMapping(
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "List existing namespaces")
    public CompletionStage<Set<NamespaceInfo>> list(ServerWebExchange exchange) {
        User user = ContextUtils.getUser(exchange);

        return core
            .namespaces()
            .listNamespaces(user);
    }

    @RequestMapping(
        path = "{name}/settings/access",
        method = RequestMethod.DELETE,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "Revoke access from a repository")
    public CompletionStage<GrantedAuthorization> revoke(
        @RequestParam("name") String name,
        @RequestBody NamespaceAccessRequest request,
        ServerWebExchange exchange) {

        User user = ContextUtils.getUser(exchange);
        ResourceName resourceName = ResourceName.apply(name);

        return core
            .namespaces()
            .revokeNamespaceAccess(user, resourceName, request.getPrivilege(), request.getAuthorization());
    }

}
