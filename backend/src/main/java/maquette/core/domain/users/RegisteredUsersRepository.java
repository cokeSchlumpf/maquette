package maquette.core.domain.users;

public interface RegisteredUsersRepository extends RegisteredUsersReadRepository {

    void insertOrUpdate(RegisteredUser registeredUser);

}
