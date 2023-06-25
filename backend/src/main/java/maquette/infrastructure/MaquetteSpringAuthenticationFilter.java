package maquette.infrastructure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import maquette.MaquetteDomainRegistry;
import maquette.common.Operators;
import maquette.core.application.MaquetteApplicationConfiguration;
import maquette.core.domain.users.AuthenticatedUser;
import maquette.core.domain.users.User;
import maquette.core.domain.users.rbac.DomainRole;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This interceptor checks for a headers injected by Maquette Authentication Proxy.
 */
@Component
@AllArgsConstructor
public class MaquetteSpringAuthenticationFilter implements Filter {

    MaquetteDomainRegistry domainRegistry;

    MaquetteSpringUserContext userContext;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest req) {
            var userId = req.getHeader("x-user-id");

            if (Objects.nonNull(userId) && !userContext.isUpdated()) {
                userContext.setUser(
                    HttpHeaderAuthenticatedUser.apply(
                        domainRegistry, userId, req.getHeader("x-user-roles"), req.getHeader("x-user-details")
                    )
                );
            } else if (Objects.isNull(userId)) {
                userContext.clear();
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Slf4j
    @AllArgsConstructor(staticName = "apply")
    private static class HttpHeaderAuthenticatedUser implements User {

        private final MaquetteDomainRegistry registry;

        @SuppressWarnings("unused")
        private String userId;

        @SuppressWarnings("unused")
        private String userRoles;

        private String userDetails;

        private User delegate;

        public static HttpHeaderAuthenticatedUser apply(
            MaquetteDomainRegistry registry,
            String userId,
            String userRoles,
            String userDetails
        ) {
            return HttpHeaderAuthenticatedUser.apply(
                registry, userId, userRoles, userDetails, null
            );
        }

        private void loadDelegate() {
            var json = new String(Base64
                .getDecoder()
                .decode(userDetails), StandardCharsets.UTF_8);

            var userDetailsObj = Operators.ignoreExceptionsWithDefault(() ->
                registry.getObjectMapper().readValue(json, UserDetails.class),
                UserDetails.fake(),
                log
            );

            var maybeRegisteredUser = registry.getUsers().findOneByEmail(userDetailsObj.getEmail());

            if (maybeRegisteredUser.isPresent()) {
                delegate = maybeRegisteredUser.get();
            } else {
                delegate = AuthenticatedUser.apply(
                    userDetailsObj.getEmail(),
                    userDetailsObj.getName(),
                    userDetailsObj.getName()
                );
            }

            delegate.assignDomainRolesFromConfiguration(
                registry.getConfiguration()
            );
        }

        private User getDelegate() {
            if (Objects.isNull(delegate)) {
                loadDelegate();
            }

            return delegate;
        }

        @Override
        public Set<DomainRole> getRoles() {
            return getDelegate().getRoles();
        }

        @Override
        public String getDisplayName() {
            return getDelegate().getDisplayName();
        }

        @Override
        public void assignDomainRolesFromConfiguration(MaquetteApplicationConfiguration configuration) {
            getDelegate().assignDomainRolesFromConfiguration(configuration);
        }

        @Override
        public String toString() {
            var json = new String(Base64
                .getDecoder()
                .decode(userDetails), StandardCharsets.UTF_8);

            return "HttpHeaderAuthenticatedUser(" +
                "userId=" + this.userId + ", " +
                "userRoles=" + this.userRoles + ", " +
                "userDetails=" + json + ", " +
                "delegate=" + this.delegate + ")";
        }
    }

    @Value
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserDetails {

        private static final String USERNAME = "username";
        private static final String NAME = "name";
        private static final String EMAIL = "email";
        private static final String ROLES = "roles";

        @With
        @JsonProperty(USERNAME)
        String username;

        @With
        @JsonProperty(NAME)
        String name;

        @With
        @JsonProperty(EMAIL)
        String email;

        @With
        @JsonProperty(ROLES)
        List<String> roles;

        @JsonCreator
        public static UserDetails apply(@JsonProperty(USERNAME) String username, @JsonProperty(NAME) String name,
                                        @JsonProperty(EMAIL) String email, @JsonProperty(ROLES) List<String> roles) {
            return new UserDetails(username, name, email, roles);
        }

        public static UserDetails fake(String username) {
            return apply(username, "name", "email", Lists.newArrayList());
        }

        public static UserDetails fake() {
            return fake("fake");
        }

    }

}
