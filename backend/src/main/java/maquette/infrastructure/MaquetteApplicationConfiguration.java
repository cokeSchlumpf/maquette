package maquette.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.MaquetteDomainRegistry;
import maquette.infrastructure.repositories.RegisteredUsersMongoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class MaquetteApplicationConfiguration {

    @Bean
    public MaquetteDomainRegistry getMaquetteDomainRegistry(
        RegisteredUsersMongoRepository users,
        ObjectMapper objectMapper
    ) {
        // TODO Add required dependencies.
        return MaquetteDomainRegistry.apply(users, null, objectMapper);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .build();
    }

}
