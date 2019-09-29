package maquette.cucumber;

import java.util.Map;

import com.google.common.collect.Maps;
import com.typesafe.config.ConfigFactory;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import maquette.controller.domain.values.iam.User;
import maquette.util.TestSetup;

public final class TestContext {

    private TestSetup setup;

    private Map<String, User> users;

    public TestContext() {
        this.users = Maps.newHashMap();
    }

    public TestSetup getSetup() {
        return setup;
    }

    public User getUser(String user) {
        return users.get(user);
    }

    public TestContext withUser(User user) {
        users.put(user.getUserId().getId(), user);
        return this;
    }

    @Before
    public void init() {
        this.setup = TestSetup.apply();
    }

    @After
    public void after() {
        this.setup.getApp().terminate();
    }
}
