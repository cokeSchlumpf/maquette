package maquette.core.domain.users;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import maquette.common.AggregateRoot;
import maquette.core.domain.users.events.RegisteredUserRegisteredEvent;
import maquette.core.domain.users.exceptions.UserAlreadyExistsException;
import maquette.core.domain.users.rbac.DomainPermission;
import maquette.core.domain.users.rbac.DomainRole;

import java.time.Instant;
import java.util.*;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegisteredUser extends AggregateRoot<String, RegisteredUser> implements User {

    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String LAST_LOGIN = "lastLogin";
    private static final String DOMAIN_ROLES = "domainRoles";

    /**
     * The unique idempotent id of the user.
     */
    @JsonProperty(ID)
    String id;

    /**
     * The unique email-address of the user.
     */
    @JsonProperty(EMAIL)
    String email;

    /**
     * The first name of the user.
     */
    @JsonProperty(FIRST_NAME)
    String firstName;

    /**
     * The last name of the user.
     */
    @JsonProperty(LAST_NAME)
    String lastName;

    /**
     * Nullable. The time when the user last logged in, into the application.
     */
    @JsonProperty(LAST_LOGIN)
    Instant lastLogin;

    /**
     * The roles assigned to the user.
     */
    @JsonProperty(DOMAIN_ROLES)
    Set<DomainRole> domainRoles;

    @JsonCreator
    public static RegisteredUser apply(
        @JsonProperty(ID) String id,
        @JsonProperty(EMAIL) String email,
        @JsonProperty(FIRST_NAME) String firstName,
        @JsonProperty(LAST_NAME) String lastName,
        @JsonProperty(LAST_LOGIN) Instant lastLogin,
        @JsonProperty(DOMAIN_ROLES) Set<DomainRole> domainRoles
    ) {
        if (Objects.isNull(domainRoles)) {
            domainRoles = Set.of();
        }

        return new RegisteredUser(
            id, email, firstName, lastName, lastLogin, Set.copyOf(domainRoles)
        );
    }

    public static RegisteredUser createNew(
        String email,
        String password,
        String firstName,
        String lastName,
        Set<DomainRole> domainRoles
    ) {
        if (Objects.isNull(domainRoles)) {
            domainRoles = Set.of();
        }

        var id = UUID.randomUUID().toString();

        return new RegisteredUser(
            id, email, firstName, lastName, Instant.now(), Set.copyOf(domainRoles)
        );
    }

    public static RegisteredUser fake() {
        return RegisteredUser.apply("123", "info@bar.de", "Egon", "Olsen", Instant.now(), Set.of());
    }

    /**
     * Initially creates (persists) this registered user instance.
     *
     * @param users The repository to persist the information.
     */
    public void register(
        RegisteredUsersRepository users
    ) {
        var existing = users.findOneByEmail(this.email);

        if (existing.isPresent() && !existing.get().id.equals(this.id)) {
            throw UserAlreadyExistsException.apply();
        } else {
            this.registerEvent(RegisteredUserRegisteredEvent.apply(this));
            users.insertOrUpdate(this);
        }
    }

    public String getName() {
        return getFirstName() + " " + getLastName();
    }

    public Optional<Instant> getLastLogin() {
        return Optional.ofNullable(lastLogin);
    }

    @Override
    public boolean hasPermission(DomainPermission permission) {
        var allPermissions = getPermissions();

        return allPermissions
            .stream()
            .anyMatch(p -> p.equals(permission));
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    private List<DomainPermission> getPermissions() {
        return this
            .domainRoles
            .stream()
            .flatMap(role -> role.getPermissions().stream())
            .toList();
    }

}
