package maquette.controller.domain.values.namespace;

import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NamespaceACL {

    private final GrantedAuthorization owner;

    private final Set<NamespaceGrant> grants;

    @JsonCreator
    public static NamespaceACL apply(
        @JsonProperty("owner") GrantedAuthorization owner,
        @JsonProperty("grants") Set<NamespaceGrant> grants) {

        return new NamespaceACL(owner, ImmutableSet.copyOf(grants));
    }

    public Optional<NamespaceGrant> findGrant(Authorization authorization, NamespacePrivilege privilege) {
        return this.grants
            .stream()
            .filter(
                grant ->
                    grant.getAuthorization().getAuthorization().equals(authorization) &&
                    grant.getPrivilege().equals(privilege))
            .findAny();
    }

    public NamespaceACL withGrant(GrantedAuthorization authorization, NamespacePrivilege privilege) {
        return withGrant(NamespaceGrant.apply(authorization, privilege));
    }

    public NamespaceACL withGrant(NamespaceGrant grant) {
        Set<NamespaceGrant> grants = Sets.newHashSet(this.grants);
        grants.add(grant);
        return apply(owner, grants);
    }

    public NamespaceACL withOwner(GrantedAuthorization owner) {
        return apply(owner, grants);
    }

    public NamespaceACL withoutGrant(Authorization authorization, NamespacePrivilege privilege) {
        return this.grants
            .stream()
            .filter(
                grant ->
                    grant.getAuthorization().getAuthorization().equals(authorization) &&
                    grant.getPrivilege().equals(privilege))
            .findFirst()
            .map(this::withoutGrant)
            .orElse(this);
    }

    public NamespaceACL withoutGrant(NamespaceGrant grant) {
        Set<NamespaceGrant> grants = Sets.newHashSet(this.grants);
        grants.remove(grant);
        return apply(owner, grants);
    }

}
