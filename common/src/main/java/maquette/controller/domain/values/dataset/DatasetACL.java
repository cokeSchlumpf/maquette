package maquette.controller.domain.values.dataset;

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

    @Deprecated
    public static DatasetACL apply(GrantedAuthorization owner, Set<DatasetGrant> grants) {
        return apply(owner, grants, false);
    }

    public boolean canConsume(User user) {
        return isConsumer(user) || isOwner(user) || isAdmin(user);
    }

    public boolean canGrantDatasetAccess(User user) {
        return isAdmin(user) || isOwner(user);
    }

    public boolean canManage(User user) {
        return isAdmin(user) || isOwner(user);
    }

    public boolean canReadDetails(User user) {
        return !isPrivate || isAdmin(user) || isConsumer(user) || isProducer(user) || isOwner(user);
    }

    public boolean canProduce(User user) {
        return isProducer(user) || isOwner(user) ||isAdmin(user);
    }

    public boolean canRevokeDatasetAccess(User user) {
        return isAdmin(user) || isOwner(user);
    }

    public Optional<DatasetGrant> findGrant(User user, DatasetPrivilege privilege) {
        return this.grants
            .stream()
            .filter(grant -> grant.getAuthorization().getAuthorization().hasAuthorization(user) &&
                             grant.getPrivilege().equals(privilege))
            .findFirst();
    }

    public Optional<DatasetGrant> findGrant(Authorization authorization, DatasetPrivilege privilege) {
        return this.grants
            .stream()
            .filter(
                grant ->
                    grant.getAuthorization().getAuthorization().equals(authorization) &&
                    grant.getPrivilege().equals(privilege))
            .findAny();
    }

    private boolean isAdmin(User user) {
        return user.isAdministrator() || findGrant(user, DatasetPrivilege.ADMIN).isPresent();
    }

    private boolean isConsumer(User user) {
        return findGrant(user, DatasetPrivilege.CONSUMER).isPresent();
    }

    private boolean isOwner(User user) {
        return owner.getAuthorization().hasAuthorization(user);
    }

    private boolean isProducer(User user) {
        return findGrant(user, DatasetPrivilege.PRODUCER).isPresent();
    }

    public DatasetACL withGrant(GrantedAuthorization authorization, DatasetPrivilege privilege) {
        return withGrant(DatasetGrant.apply(authorization, privilege));
    }

    public DatasetACL withGrant(DatasetGrant grant) {
        Set<DatasetGrant> grants = Sets.newHashSet(this.grants);
        grants.add(grant);
        return apply(owner, grants, isPrivate);
    }

    public DatasetACL withPrivacy(boolean isPrivate) {
        return apply(owner, grants, isPrivate);
    }

    public DatasetACL withOwner(GrantedAuthorization owner) {
        return apply(owner, grants);
    }

    public DatasetACL withoutGrant(Authorization authorization, DatasetPrivilege privilege) {
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

    public DatasetACL withoutGrant(DatasetGrant grant) {
        Set<DatasetGrant> grants = Sets.newHashSet(this.grants);
        grants.remove(grant);
        return apply(owner, grants, isPrivate);
    }

}
