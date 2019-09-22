package maquette.sdk.sample;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import maquette.sdk.dsl.DatasetConsumerFactory;
import maquette.sdk.util.MaquetteConfiguration;

public class SampleConsumerApplication {

    public static void main(String ...args) {
        final MaquetteConfiguration config = MaquetteConfiguration
            .apply() // initialize with defaults
            .withBaseUrl("http://localhost:8080/api/v1")
            .withUser("hippo")
            .withToken(null);

        final DatasetConsumerFactory dcf = DatasetConsumerFactory
            .apply()
            .withMaquette(config);

        final String namespace = "_";
        final String dataset = "my-data";

        final ActorSystem system = ActorSystem.create();
        final Materializer materializer = ActorMaterializer.create(system);

        Source<Country, NotUsed> countries = dcf.createSource(namespace, dataset, Country.class);

        countries
            .map(c -> {
                System.out.println(c);
                return c;
            })
            .runWith(Sink.ignore(), materializer)
            .thenRun(system::terminate);
    }

}
