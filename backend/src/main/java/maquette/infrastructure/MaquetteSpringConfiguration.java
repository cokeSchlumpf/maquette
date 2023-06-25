package maquette.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.MaquetteDomainRegistry;
import maquette.core.application.MaquetteApplicationConfiguration;
import maquette.infrastructure.repositories.RegisteredUsersMongoRepository;
import maquette.infrastructure.repositories.WorkspacesMongoRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class MaquetteSpringConfiguration {

    @Bean
    public MaquetteDomainRegistry getMaquetteDomainRegistry(
        MaquetteApplicationConfiguration config,
        RegisteredUsersMongoRepository users,
        WorkspacesMongoRepository workspaces,
        ObjectMapper objectMapper
    ) {

        return MaquetteDomainRegistry.apply(
            config, users, workspaces, objectMapper
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .build();
    }

    @Bean
    @ConfigurationProperties(prefix = "maquette")
    public MaquetteApplicationConfiguration applicationConfiguration() {
        return new MaquetteApplicationConfiguration();
    }

}
