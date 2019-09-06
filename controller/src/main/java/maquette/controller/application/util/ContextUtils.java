package maquette.controller.application.util;

import java.util.List;

import org.springframework.web.server.ServerWebExchange;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import maquette.controller.domain.values.iam.AnonymousUser;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.User;

public final class ContextUtils {

    private ContextUtils() {

    }

    public static User getUser(ServerWebExchange exchange) {
        final String userId = exchange.getRequest().getHeaders().getFirst("x-user-id");
        final String rolesAllowed = exchange.getRequest().getHeaders().getFirst("x-user-roles");

        final List<String> roles;

        if (rolesAllowed != null) {
            roles = Lists.newArrayList(rolesAllowed.split(","));
        } else {
            roles = Lists.newArrayList();
        }

        if (rolesAllowed != null) {
            return AuthenticatedUser.apply(userId, userId, Sets.newHashSet(rolesAllowed));
        } else {
            return AnonymousUser.apply(Sets.newHashSet(roles));
        }
    }

}
