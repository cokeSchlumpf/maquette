package maquette.controller.application.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import maquette.controller.adapters.storage.FileSystemStorageAdapter;
import maquette.controller.adapters.storage.InMemoryDataStorageAdapter;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.ports.DataStorageAdapter;
import maquette.controller.domain.util.databind.ObjectMapperFactory;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public CoreApplication getApplication() {
        DataStorageAdapter storageAdapter = FileSystemStorageAdapter.apply();
        return CoreApplication.apply(storageAdapter);
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return ObjectMapperFactory.apply().create(true);
    }

}
