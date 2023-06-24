package maquette.core.domain.users;

import maquette.common.DomainRepository;

import java.util.Optional;

public interface RegisteredUsersReadRepository extends DomainRepository {

    Optional<RegisteredUser> findOneByEmail(String email);

    default RegisteredUser getOneByEmail(String email) {
        return findOneByEmail(email).orElseThrow();
    }

}
