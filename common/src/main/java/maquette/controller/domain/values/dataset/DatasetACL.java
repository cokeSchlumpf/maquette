package maquette.controller.domain.values.dataset;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasetACL {

    private static final String OWNER = "owner";
    private static final String GRANTS = "grants";
    private static final String IS_PRIVATE = "private";

    @JsonProperty(OWNER)
    private final GrantedAuthorization owner;

    @JsonProperty(GRANTS)
    private final Set<DatasetGrant> grants;

    @JsonProperty(IS_PRIVATE)
    private final boolean isPrivate;

    @JsonCreator
    public static DatasetACL apply(
        @JsonProperty(OWNER) GrantedAuthorization owner,
        @JsonProperty(GRANTS) Set<DatasetGrant> grants,
        @JsonProperty(IS_PRIVATE) boolean isPrivate) {

        return new DatasetACL(owner, ImmutableSet.copyOf(grants), isPrivate);
    }

    public boolean canConsume(User user) {
        return isConsumer(user) || isOwner(user) || isAdmin(user);
    }

    public boolean canGrantDatasetAccess(User user) {
        return isAdmin(user) || isOwner(user);
    }

    public boolean canFind(User user) {
        return !isPrivate || canReadDetails(user);
    }

    public boolean canManage(User user) {
        return isAdmin(user) || isOwner(user);
    }

    public boolean canReadDetails(User user) {
        return !isPrivate || isAdmin(user) || isConsumer(user) || isProducer(user) || isOwner(user);
    }

    public boolean canProduce(User user) {
        return isProducer(user) || isOwner(user) || isAdmin(user);
    }

    public boolean canRevokeDatasetAccess(User user) {
        return isAdmin(user) || isOwner(user);
    }

    public Optional<DatasetGrant> findGrantById(UID id) {
        return this.grants
            .stream()
            .filter(g -> g.getId().equals(id))
            .findFirst();
    }

    public Optional<DatasetGrant> findGrantByAuthorizationAndPrivilege(Authorization authorization, DatasetPrivilege privilege) {
        return this
            .grants
            .stream()
            .filter(grant -> grant.getGrantFor().equals(authorization) &&
                             grant.getGrant().equals(privilege))
            .findFirst();
    }

    public Optional<DatasetGrant> findGrantByAuthorizationAndPrivilegeAndNotClosed(Authorization authorization, DatasetPrivilege privilege) {
        return this
            .grants
            .stream()
            .filter(grant -> grant.getGrantFor().equals(authorization) &&
                             grant.getGrant().equals(privilege))
            .filter(grant -> !grant.isClosed())
            .findFirst();
    }

    public Optional<DatasetMember> findMemberByUserAndPrivilege(User user, DatasetPrivilege privilege) {
        return this.getMembers()
                   .stream()
                   .filter(grant -> grant.getAuthorization().getAuthorization().hasAuthorization(user) &&
                                    grant.getPrivilege().equals(privilege))
                   .findFirst();
    }

    public Optional<DatasetMember> findMemberByAuthorizationAndPrivilege(Authorization authorization, DatasetPrivilege privilege) {
        return this.getMembers()
                   .stream()
                   .filter(
                       grant ->
                           grant.getAuthorization().getAuthorization().equals(authorization) &&
                           grant.getPrivilege().equals(privilege))
                   .findAny();
    }

    private boolean isAdmin(User user) {
        return user.isAdministrator() || findMemberByUserAndPrivilege(user, DatasetPrivilege.ADMIN).isPresent();
    }

    private boolean isConsumer(User user) {
        return findMemberByUserAndPrivilege(user, DatasetPrivilege.CONSUMER).isPresent();
    }

    private boolean isOwner(User user) {
        return owner.getAuthorization().hasAuthorization(user);
    }

    private boolean isProducer(User user) {
        return findMemberByUserAndPrivilege(user, DatasetPrivilege.PRODUCER).isPresent();
    }

    public Set<DatasetMember> getMembers() {
        return ImmutableSet.copyOf(
            this.grants
                .stream()
                .map(DatasetGrant::asDatasetMember)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet()));
    }

    public DatasetACL withGrant(DatasetGrant grant) {
        Set<DatasetGrant> grants = Sets
            .newHashSet(this.grants)
            .stream()
            .filter(e -> !e.getId().equals(grant.getId()))
            .collect(Collectors.toSet());

        grants.add(grant);
        return apply(owner, grants, isPrivate);
    }

    public DatasetACL withPrivacy(boolean isPrivate) {
        return apply(owner, grants, isPrivate);
    }

    public DatasetACL withOwner(GrantedAuthorization owner) {
        return apply(owner, grants, isPrivate);
    }

}
