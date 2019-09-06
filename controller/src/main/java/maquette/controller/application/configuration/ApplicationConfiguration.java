package maquette.controller.application.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import maquette.controller.adapters.InMemoryDataStorageAdapter;
import maquette.controller.domain.CoreApplication;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public CoreApplication getApplication() {
        InMemoryDataStorageAdapter storageAdapter = InMemoryDataStorageAdapter.apply();
        return CoreApplication.apply(storageAdapter);
    }

}
