package maquette.controller.domain.values.iam;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthenticatedUser implements User {

    private final String id;

    private final String name;

    private final ImmutableSet<String> roles;

    @JsonCreator
    public static AuthenticatedUser apply(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("roles") Set<String> roles) {

        return new AuthenticatedUser(id, name, ImmutableSet.copyOf(roles));
    }

    public static AuthenticatedUser apply(String id, String name, String... roles) {
        return apply(id, name, Sets.newHashSet(roles));
    }

    @Override
    @JsonIgnore
    public UserId getUserId() {
        return UserId.apply(getId());
    }

    @Override
    @JsonIgnore
    public String getDisplayName() {
        return getName();
    }

    public Set<String> getRoles() {
        return roles;
    }

    @Override
    public boolean isAdministrator() {
        return roles.contains("admin");
    }

    @Override
    public boolean isAuditor() {
        return false;
    }

}
