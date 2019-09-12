package maquette.controller.domain.values.iam;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenAuthenticatedUser implements User {

    private static final String TOKEN = "token";
    private static final String ROLES = "roles";

    @JsonProperty(TOKEN)
    private final TokenDetails token;

    private final Set<String> roles;

    @JsonCreator
    public static TokenAuthenticatedUser apply(
        @JsonProperty(TOKEN) TokenDetails token,
        @JsonProperty(ROLES) Set<String> roles) {

        return new TokenAuthenticatedUser(token, ImmutableSet.copyOf(roles));
    }

    public static TokenAuthenticatedUser apply(TokenDetails token) {
        return apply(token, Sets.newHashSet());
    }

    @Override
    public UserId getUserId() {
        return token.getOwner();
    }

    @Override
    public String getDisplayName() {
        return token.getOwner().getId();
    }

    @Override
    public Set<String> getRoles() {
        return roles;
    }

    @Override
    public boolean isAdministrator() {
        return false;
    }

    @Override
    public boolean isAuditor() {
        return false;
    }

    public TokenAuthenticatedUser withRoles(Set<String> roles) {
        return apply(token, roles);
    }

}
