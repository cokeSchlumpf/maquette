package maquette.controller.domain.values.iam;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.exceptions.InvalidTokenException;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDetails {

    private static final String ID = "id";
    private static final String ACCESS_TOKENS = "access-tokens";
    private static final String NAMESPACE = "namespace";

    @JsonProperty(ID)
    private final UserId id;

    @JsonProperty(ACCESS_TOKENS)
    private final Set<Token> accessTokens;

    @JsonProperty(NAMESPACE)
    private final ResourceName namespace;

    @JsonCreator
    public static UserDetails apply(
        @JsonProperty(ID) UserId id,
        @JsonProperty(ACCESS_TOKENS) Set<Token> accessTokens,
        @JsonProperty(NAMESPACE) ResourceName namespace) {

        return new UserDetails(id, ImmutableSet.copyOf(accessTokens), namespace);
    }

    public static UserDetails apply(UserId id, Set<Token> accessTokens) {
        return apply(id, accessTokens, null);
    }

    public TokenAuthenticatedUser authenticateWithToken(UID secret) {

        return accessTokens
            .stream()
            .filter(token -> token.getSecret().equals(secret))
            .map(token -> TokenAuthenticatedUser.apply(token.getDetails()))
            .findAny()
            .orElseThrow(() -> InvalidTokenException.apply(id.getId()));
    }

    public boolean canViewPersonalProperties(User executor) {
        return (executor instanceof AuthenticatedUser && executor.getUserId().equals(id)) || executor.isAdministrator();
    }

    public boolean canManageTokens(User executor) {
        return (executor instanceof AuthenticatedUser && executor.getUserId().equals(id)) || executor.isAdministrator();
    }

    public Optional<ResourceName> getNamespace() {
        return Optional.ofNullable(namespace);
    }

    public UserDetails withNamespace(ResourceName namespace) {
        return apply(id, accessTokens, namespace);
    }

    public UserDetails withToken(Token token) {
        Set<Token> newTokens = accessTokens
            .stream()
            .filter(existingToken -> !existingToken.getDetails().getName().equals(token.getDetails().getName()))
            .collect(Collectors.toSet());

        newTokens.add(token);

        return apply(id, newTokens);
    }

    public UserDetails withoutToken(ResourceName name) {
        Set<Token> newTokens = accessTokens
            .stream()
            .filter(existingToken -> !existingToken.getDetails().getName().equals(name))
            .collect(Collectors.toSet());

        return apply(id, newTokens);
    }

}
