package maquette;

import com.github.dockerjava.api.model.ContainerMount;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import maquette.test.containers.MaquetteAuthContainer;
import maquette.test.containers.MongoContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.test.context.support.TestPropertySourceUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
public class MaquetteApplicationTest {

    public static void main(String... args) {
        var mongo = MongoContainer.apply();
        var auth = MaquetteAuthContainer.apply(8080);

        mongo.start();
        auth.start();

        var app = new SpringApplication(MaquetteApplication.class);

        app.addInitializers(applicationContext -> {
            applicationContext.getEnvironment().setActiveProfiles("test");

            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "spring.data.mongodb.host=localhost",
                "spring.data.mongodb.port=" + mongo.getPort(),
                "spring.data.mongodb.database=app",
                "spring.data.mongodb.username=app",
                "spring.data.mongodb.password=password"
            );
        });

        Runtime.getRuntime().addShutdownHook(new Thread(MaquetteApplicationTest::clean));
        app.run();

        log.info("Local test environment started. Visit {} to access Mars UI.", auth.getUrl());
    }

    private static void clean() {
        var config = DefaultDockerClientConfig
            .createDefaultConfigBuilder()
            .build();

        var httpClient = new ZerodepDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .build();

        var client = DockerClientImpl.getInstance(config, httpClient);

        client
            .listContainersCmd()
            .withFilter("name", List.of("mq--*", "mlflow--*", "mrs--"))
            .withShowAll(true)
            .exec()
            .forEach(container -> {
                try {
                    log.info("Removing container `" + String.join(", ", container.getNames()) + "` ...");

                    var volumes = container
                        .getMounts()
                        .stream()
                        .filter(mnt -> Objects.equals(mnt.getDriver(), "local"))
                        .map(ContainerMount::getName)
                        .toList();

                    client.removeContainerCmd(container.getId()).withForce(true).exec();

                    volumes.forEach(volume -> {
                        try {
                            client.removeVolumeCmd(volume);
                        } catch (Exception e) {
                            log.warn("Exception occurred while removing volume.", e);
                        }
                    });
                } catch (Exception e) {
                    log.warn("Exception occurred while removing container.", e);
                }
            });

        client
            .listNetworksCmd()
            .withNameFilter("mq--*", "mlflow--*", "mrs--")
            .exec()
            .forEach(network -> {
                log.info("Removing network `" + network.getName() + "` ...");
                try {
                    client.removeNetworkCmd(network.getId()).exec();
                } catch (Exception e) {
                    // Ignore
                }
            });

    }

}
