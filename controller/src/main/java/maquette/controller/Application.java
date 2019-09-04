package maquette.controller;

import maquette.controller.adapters.InMemoryDataStorageAdapter;
import maquette.controller.domain.CoreApplication;

public class Application {

    public static void main(String ...args) throws InterruptedException {
        InMemoryDataStorageAdapter storageAdapter = InMemoryDataStorageAdapter.apply();
        CoreApplication app = CoreApplication.apply(storageAdapter);
        Thread.sleep(10000);
        app.terminate();
    }

}
