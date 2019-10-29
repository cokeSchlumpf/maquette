package maquette.controller.domain.values.project;

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
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectACL {

    private static final String OWNER = "owner";
    private static final String GRANTS = "grants";
    private static final String IS_PRIVATE = "private";

    private final GrantedAuthorization owner;

    private final Set<ProjectGrant> grants;

    private final boolean isPrivate;

    @JsonCreator
    public static ProjectACL apply(
        @JsonProperty(OWNER) GrantedAuthorization owner,
        @JsonProperty(GRANTS) Set<ProjectGrant> grants,
        @JsonProperty(IS_PRIVATE) boolean isPrivate) {

        return new ProjectACL(owner, ImmutableSet.copyOf(grants), isPrivate);
    }

    public boolean canConsume(User user) {
        return isConsumer(user) || isOwner(user) || isMember(user) || isAdmin(user);
    }

    public boolean canCreateDataset(User user) {
        return isOwner(user) || isAdmin(user) || isMember(user);
    }

    public boolean canDeleteDataset(User user) {
        return isOwner(user) || isAdmin(user) || isMember(user);
    }

    public boolean canDeleteNamespace(User user) {
        return isOwner(user) || isAdmin(user);
    }

    public boolean canFind(User user) {
        return !isPrivate() || canReadDetails(user);
    }

    public boolean canGrantNamespaceAccess(User user) {
        return isOwner(user) || isAdmin(user);
    }

    public boolean canManage(User user) {
        return isOwner(user) || isAdmin(user);
    }

    public boolean canProduce(User user) {
        return isProducer(user) || isOwner(user) || isMember(user) || isAdmin(user);
    }

    public boolean canReadDetails(User user) {
        return isOwner(user) || isAdmin(user) || isProducer(user) || isConsumer(user) || isMember(user);
    }

    public boolean canReadResourceDetails(User user) {
        return isOwner(user) || isAdmin(user) || isProducer(user) || isConsumer(user) || isMember(user);
    }

    public boolean canRevokeNamespaceAccess(User user) {
        return isOwner(user) || isAdmin(user);
    }

    public Optional<ProjectGrant> findGrant(User user, ProjectPrivilege privilege) {
        return this.grants
            .stream()
            .filter(grant -> grant.getAuthorization().getAuthorization().hasAuthorization(user) &&
                             grant.getPrivilege().equals(privilege))
            .findFirst();
    }

    public Optional<ProjectGrant> findGrant(Authorization authorization, ProjectPrivilege privilege) {
        return this.grants
            .stream()
            .filter(
                grant ->
                    grant.getAuthorization().getAuthorization().equals(authorization) &&
                    grant.getPrivilege().equals(privilege))
            .findAny();
    }

    private boolean isAdmin(User user) {
        return user.isAdministrator() || findGrant(user, ProjectPrivilege.ADMIN).isPresent();
    }

    private boolean isConsumer(User user) {
        return findGrant(user, ProjectPrivilege.CONSUMER).isPresent();
    }

    private boolean isMember(User user) {
        return findGrant(user, ProjectPrivilege.MEMBER).isPresent();
    }

    private boolean isOwner(User user) {
        return owner.getAuthorization().hasAuthorization(user);
    }

    private boolean isProducer(User user) {
        return findGrant(user, ProjectPrivilege.PRODUCER).isPresent();
    }

    public ProjectACL withGrant(GrantedAuthorization authorization, ProjectPrivilege privilege) {
        return withGrant(ProjectGrant.apply(authorization, privilege));
    }

    public ProjectACL withGrant(ProjectGrant grant) {
        Set<ProjectGrant> grants = Sets.newHashSet(this.grants);
        grants.add(grant);
        return apply(owner, grants, isPrivate);
    }

    public ProjectACL withOwner(GrantedAuthorization owner) {
        return apply(owner, grants, isPrivate);
    }

    public ProjectACL withPrivacy(boolean isPrivate) {
        return apply(owner, grants, isPrivate);
    }

    public ProjectACL withoutGrant(Authorization authorization, ProjectPrivilege privilege) {
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

    public ProjectACL withoutGrant(ProjectGrant grant) {
        Set<ProjectGrant> grants = Sets.newHashSet(this.grants);
        grants.remove(grant);
        return apply(owner, grants, isPrivate);
    }

}
