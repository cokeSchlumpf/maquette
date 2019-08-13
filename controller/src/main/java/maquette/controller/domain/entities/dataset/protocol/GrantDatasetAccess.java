package maquette.controller.domain.entities.dataset.protocol;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.User;
import netscape.security.Privilege;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GrantDatasetAccess {

    private final User executor;

    private final Set<Privilege> grant;

    private final Authorization grantFor;

    public static GrantDatasetAccess apply(User executor, Set<Privilege> grant, Authorization grantFor) {
        return new GrantDatasetAccess(executor, ImmutableSet.copyOf(grant), grantFor);
    }

}
