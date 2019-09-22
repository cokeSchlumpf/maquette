package maquette.sdk.sample;

import java.util.concurrent.ExecutionException;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;
import maquette.sdk.dsl.DatasetProducerFactory;
import maquette.sdk.util.MaquetteConfiguration;

public class SampleProducerApplication {

    public static void main(String ...args) throws ExecutionException, InterruptedException {
        final MaquetteConfiguration config = MaquetteConfiguration
            .apply() // initialize with defaults
            .withBaseUrl("http://localhost:8080/api/v1")
            .withUser("hippo")
            .withToken(null);

        final DatasetProducerFactory dsf = DatasetProducerFactory
            .apply() // initialize with defaults
            .withMaquette(config);

        final String namespace = "_"; // "_" defaults to users default namespace
        final String dataset = "my-data";

        final ActorSystem system = ActorSystem.apply("sample");

        Source
            .range(1,100)
            .mapConcat(i -> Country.getSample())
            .runWith(
                dsf.createSink(namespace, dataset, Country.class),
                ActorMaterializer.create(system))
            .thenRun(system::terminate)
            .toCompletableFuture()
            .get();
    }

}
