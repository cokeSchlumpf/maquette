package maquette.controller.domain.values.iam;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenAuthenticatedUser implements User {

    private static final String TOKEN = "token";

    @JsonProperty(TOKEN)
    private final TokenDetails token;

    @JsonCreator
    public static TokenAuthenticatedUser apply(
        @JsonProperty(TOKEN) TokenDetails token) {

        return new TokenAuthenticatedUser(token);
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
        return Sets.newHashSet();
    }

    @Override
    public boolean isAdministrator() {
        return false;
    }

    @Override
    public boolean isAuditor() {
        return false;
    }

}
