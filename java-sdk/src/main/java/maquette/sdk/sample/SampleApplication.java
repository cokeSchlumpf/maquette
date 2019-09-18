package maquette.sdk.sample;

import java.util.concurrent.ExecutionException;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;
import maquette.sdk.dsl.DatasetProducerFactory;

public class SampleApplication {

    public static void main(String ...args) throws ExecutionException, InterruptedException {
        ActorSystem system = ActorSystem.apply("sample");

        Source
            .range(1,100)
            .mapConcat(i -> Country.getSample())
            .runWith(
                DatasetProducerFactory.apply().createSink("_", "my-data", Country.class),
                ActorMaterializer.create(system))
            .thenRun(system::terminate)
            .toCompletableFuture()
            .get();
    }

}
