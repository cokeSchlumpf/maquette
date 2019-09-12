package maquette.controller.application.util;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.AnonymousUser;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserId;

@Component
public class ContextUtils {

    private final CoreApplication app;

    private ContextUtils(CoreApplication app) {
        this.app = app;
    }

    public CompletionStage<User> getUser(ServerWebExchange exchange) {
        final String userId = exchange.getRequest().getHeaders().getFirst("x-user-id");
        final String userToken = exchange.getRequest().getHeaders().getFirst("x-user-token");
        final String rolesAllowed = exchange.getRequest().getHeaders().getFirst("x-user-roles");

        final List<String> roles;

        if (rolesAllowed != null) {
            roles = Lists.newArrayList(rolesAllowed.split(","));
        } else {
            roles = Lists.newArrayList();
        }

        HashSet<String> rolesSet = Sets.newHashSet(roles);

        if (userToken != null) {
            return app
                .users()
                .authenticate(UserId.apply(userId), UID.apply(userToken))
                .thenApply(user -> user.withRoles(rolesSet));
        } else if (userId != null) {
            return CompletableFuture.completedFuture(AuthenticatedUser.apply(userId, userId, rolesSet));
        } else {
            return CompletableFuture.completedFuture(AnonymousUser.apply(rolesSet));
        }
    }

}
