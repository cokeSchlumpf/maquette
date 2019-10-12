package maquette.controller.application.configuration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import maquette.controller.adapters.storage.FileSystemStorageAdapter;
import maquette.controller.adapters.storage.InMemoryDataStorageAdapter;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.SampleDataProvider;
import maquette.controller.domain.ports.DataStorageAdapter;
import maquette.controller.domain.util.databind.ObjectMapperFactory;

@Configuration
public class ApplicationConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Bean
    public CoreApplication getApplication() {
        DataStorageAdapter storageAdapter = FileSystemStorageAdapter.apply();
        CoreApplication app = CoreApplication.apply(storageAdapter);

        Path journal = new File("./journal").toPath();

        if (!Files.exists(journal)) {
            LOG.info("Initialize with sample data");
            SampleDataProvider.apply(app).initialize();
        } else {
            LOG.info(String.format("Skipping sample data initialization. %s already exists.", journal.toAbsolutePath()));
        }

        return app;
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return ObjectMapperFactory.apply().create(true);
    }

}
