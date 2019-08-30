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
import maquette.controller.domain.values.namespace.NamespaceGrant;
import maquette.controller.domain.values.namespace.NamespacePrivilege;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasetACL {

    private final GrantedAuthorization owner;

    private final Set<DatasetGrant> grants;

    @JsonCreator
    public static DatasetACL apply(
        @JsonProperty("owner") GrantedAuthorization owner,
        @JsonProperty("grants") Set<DatasetGrant> grants) {

        return new DatasetACL(owner, ImmutableSet.copyOf(grants));
    }

    public boolean canChangeOwner(User user) {
        return isAdmin(user) || isOwner(user);
    }

    public boolean canGrantDatasetAccess(User user) {
        return isAdmin(user) || isOwner(user);
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
        return findGrant(user, DatasetPrivilege.ADMIN).isPresent();
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
        return apply(owner, grants);
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
        return apply(owner, grants);
    }

}
