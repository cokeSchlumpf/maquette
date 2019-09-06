package maquette.controller.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String ...args) throws InterruptedException {
        /*
        InMemoryDataStorageAdapter storageAdapter = InMemoryDataStorageAdapter.apply();
        CoreApplication app = CoreApplication.apply(storageAdapter);
        Thread.sleep(10000);
        app.terminate();
         */

        SpringApplication.run(Application.class, args);
    }

}
