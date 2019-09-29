package maquette.cucumber;

import cucumber.api.java.Before;
import maquette.util.TestSetup;

public final class TestContext {

    private TestSetup setup;

    public TestContext() {

    }

    public TestSetup getSetup() {
        return setup;
    }

    @Before
    public void init() {
        System.out.println("BEFORE");
        this.setup = TestSetup.apply();
    }

    @Before
    public void after() {
        this.setup.getApp().terminate();
        System.out.println("AFTER");
    }
}
