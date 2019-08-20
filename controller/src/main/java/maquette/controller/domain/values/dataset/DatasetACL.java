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

    public Optional<DatasetGrant> findGrant(Authorization authorization, DatasetPrivilege privilege) {
        return this.grants
            .stream()
            .filter(
                grant ->
                    grant.getAuthorization().getAuthorization().equals(authorization) &&
                    grant.getPrivilege().equals(privilege))
            .findAny();
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
