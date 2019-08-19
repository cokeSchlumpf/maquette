package maquette.controller;

import maquette.controller.domain.CoreApplication;

public class Application {

    public static void main(String ...args) throws InterruptedException {
        CoreApplication app = CoreApplication.apply();
        Thread.sleep(10000);
        app.terminate();
    }

}
