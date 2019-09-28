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
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NamespaceACL {

    private static final String OWNER = "owner";
    private static final String GRANTS = "grants";
    private static final String IS_PRIVATE = "is-private";

    private final GrantedAuthorization owner;

    private final Set<NamespaceGrant> grants;

    private final boolean isPrivate;

    @JsonCreator
    public static NamespaceACL apply(
        @JsonProperty(OWNER) GrantedAuthorization owner,
        @JsonProperty(GRANTS) Set<NamespaceGrant> grants,
        @JsonProperty(IS_PRIVATE) boolean isPrivate) {

        return new NamespaceACL(owner, ImmutableSet.copyOf(grants), isPrivate);
    }

    @Deprecated
    public static NamespaceACL apply(GrantedAuthorization owner, Set<NamespaceGrant> grants) {
        return apply(owner, grants, false);
    }

    public boolean canConsume(User user) {
        return isConsumer(user) || isOwner(user) || isMember(user);
    }

    public boolean canCreatedDataset(User user) {
        return isOwner(user) || isAdmin(user) || isMember(user);
    }

    public boolean canDeleteDataset(User user) {
        return isOwner(user) || isAdmin(user) || isMember(user);
    }

    public boolean canDeleteNamespace(User user) {
        return isOwner(user) || isAdmin(user);
    }

    public boolean canGrantNamespaceAccess(User user) {
        return isOwner(user) || isAdmin(user);
    }

    public boolean canManage(User user) {
        return isOwner(user) || isAdmin(user);
    }

    public boolean canProduce(User user) {
        return isProducer(user) || isOwner(user) || isMember(user);
    }

    public boolean canReadDetails(User user) {
        return isOwner(user) || isAdmin(user) || isProducer(user) || isConsumer(user) || isMember(user);
    }

    public boolean canRevokeNamespaceAccess(User user) {
        return isOwner(user) || isAdmin(user);
    }

    public Optional<NamespaceGrant> findGrant(User user, NamespacePrivilege privilege) {
        return this.grants
            .stream()
            .filter(grant -> grant.getAuthorization().getAuthorization().hasAuthorization(user) &&
                             grant.getPrivilege().equals(privilege))
            .findFirst();
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

    private boolean isAdmin(User user) {
        return findGrant(user, NamespacePrivilege.ADMIN).isPresent();
    }

    private boolean isConsumer(User user) {
        return findGrant(user, NamespacePrivilege.CONSUMER).isPresent();
    }

    private boolean isMember(User user) {
        return findGrant(user, NamespacePrivilege.MEMBER).isPresent();
    }

    private boolean isOwner(User user) {
        return owner.getAuthorization().hasAuthorization(user);
    }

    private boolean isProducer(User user) {
        return findGrant(user, NamespacePrivilege.PRODUCER).isPresent();
    }

    public NamespaceACL withGrant(GrantedAuthorization authorization, NamespacePrivilege privilege) {
        return withGrant(NamespaceGrant.apply(authorization, privilege));
    }

    public NamespaceACL withGrant(NamespaceGrant grant) {
        Set<NamespaceGrant> grants = Sets.newHashSet(this.grants);
        grants.add(grant);
        return apply(owner, grants, isPrivate);
    }

    public NamespaceACL withOwner(GrantedAuthorization owner) {
        return apply(owner, grants, isPrivate);
    }

    public NamespaceACL withPrivacy(boolean isPrivate) {
        return apply(owner, grants, isPrivate);
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
        return apply(owner, grants, isPrivate);
    }

}
