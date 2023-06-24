package maquette.test.containers;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import lombok.AllArgsConstructor;
import org.testcontainers.containers.GenericContainer;

@AllArgsConstructor(staticName = "apply")
public final class MaquetteAuthContainer extends GenericContainer<MaquetteAuthContainer> {

    public static final int PORT = 4200;

    public MaquetteAuthContainer(String tag) {
        super("spacereg.azurecr.io/mars-auth:" + tag);
    }

    public static MaquetteAuthContainer apply(int uiPort) {
        return apply("latest", uiPort);
    }

    public static MaquetteAuthContainer apply(String tag, int uiPort) {
        return new MaquetteAuthContainer(tag)
            .withEnv("MQ_PROXY_MODE", "dev")
            .withEnv("MQ_PROXY_URL", String.format("http://host.docker.internal:%s", uiPort))
            .withReuse(true)
            .withCreateContainerCmdModifier(cmd -> {
                    var exposedPort = ExposedPort.tcp(PORT);
                    var bindings = new Ports();
                    bindings.bind(exposedPort, Ports.Binding.bindPort(PORT));

                    var hostConfig = new HostConfig().withPortBindings(bindings);

                    cmd
                        .withName("mrs--auth")
                        .withExposedPorts(exposedPort)
                        .withHostConfig(hostConfig);
                }
            );
    }

    public String getUrl() {
        return String.format("http://%s:%s", this.getHost(), this.getMappedPort(PORT));
    }

}
