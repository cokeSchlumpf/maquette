package maquette.infrastructure.repositories;

import maquette.core.domain.users.RegisteredUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RegisteredUsersSpringDataMongoRepository extends MongoRepository<RegisteredUser, String> {

    Optional<RegisteredUser> findOneByEmail(String email);

}
