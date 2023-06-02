package maquette.core.domain.users.rbac;

import java.util.Set;


public interface DomainRole {

    String getName();

    Set<DomainPermission> getPermissions();

}
