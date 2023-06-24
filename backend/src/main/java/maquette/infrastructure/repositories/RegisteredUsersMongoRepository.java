package maquette.infrastructure.repositories;

import lombok.AllArgsConstructor;
import maquette.core.domain.users.RegisteredUser;
import maquette.core.domain.users.RegisteredUsersRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class RegisteredUsersMongoRepository implements RegisteredUsersRepository {

    private final RegisteredUsersSpringDataMongoRepository spring;

    @Override
    public Optional<RegisteredUser> findOneByEmail(String email) {
        return spring.findOneByEmail(email);
    }

    @Override
    public void insertOrUpdate(RegisteredUser registeredUser) {
        spring.save(registeredUser);
    }

}
