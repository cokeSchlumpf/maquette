# Maquette Java SDK

The Maquette Java SDK allows to implement backend integrations which produce or consume data to/ from Maquette Data Shop. The SDK is based on [Akka Streams](https://doc.akka.io/docs/akka/current/stream/index.html), an implementation of [Reactive Streams API](https://www.reactive-streams.org/). Thus you can integrate it with any other Reactive Streams Framework (e.g. [Reactor](https://projectreactor.io/)). Apart from that, the Maquette SDK also contains API entry points which can be used with plain Java 8+ API.

## Producing Data

Producing data with direct use of Akka Streams API:

```java
final MaquetteConfiguration config = MaquetteConfiguration
    .apply() // initialize with defaults
    .withBaseUrl("http://localhost:8080/api/v1")
    .withUser("hippo")
    .withToken("SECRET_TOKEN");

final DatasetProducerFactory dsf = DatasetProducerFactory
    .apply() // initialize with defaults
    .withMaquette(config);

final String namespace = "_"; // "_" defaults to users default namespace
final String dataset = "my-data";

final ActorSystem system = ActorSystem.apply("sample");

// Using the Data Shop's dataset a Sink:

Source
    .range(1,100)
    .mapConcat(i -> Country.getSample())
    .runWith(
        dsf.createSink(namespace, dataset, Country.class),
        ActorMaterializer.create(system))
    .thenRun(system::terminate)
    .toCompletableFuture()
    .get();
```

## Consuming data

Data can be consumebd by creating an Akka Stream Source via the SDK API:

```java
final MaquetteConfiguration config = MaquetteConfiguration
    .apply() // initialize with defaults
    .withBaseUrl("http://localhost:8080/api/v1")
    .withUser("hippo")
    .withToken("SECRET_TOKEN");

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
```