package maquette.sdk;

import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;

public class DatasetSinkBuilderUTest {

    private ActorSystem system;

    private Materializer materializer;

    @Before
    public void before() {
        this.system = ActorSystem.create();
        this.materializer = ActorMaterializer.create(system);
    }

    @After
    public void after() {
        if (this.system != null) {
            this.system.terminate();
        }
    }

    @Test
    public void test() throws ExecutionException, InterruptedException {

        /*
        TODO: Create FakeController mock

        List<TestType> foos = IntStream
            .range(0, 100)
            .mapToObj(i -> new TestType("foo", "bar" + i))
            .collect(Collectors.toList());

        JacksonAvroSerializer<TestType> serializer =
            JacksonAvroSerializer.apply(ObjectMapperFactory.apply().createAvroMapper(), TestType.class);

        DatasetProducerFactory<TestType> sink = DatasetProducerFactory.apply(
            "http://localhost:8080/api/v1",
            "hippo",
            "123",
            "_",
            "my-data",
            30,
            serializer,
            ObjectMapperFactory.apply().create(),
            new OkHttpClient());

        Source
            .from(foos)
            .runWith(sink.createSink(), materializer)
            .toCompletableFuture()
            .get();

         */
    }

}