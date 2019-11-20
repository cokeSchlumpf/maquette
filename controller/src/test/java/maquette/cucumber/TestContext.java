package maquette.cucumber;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.typesafe.config.ConfigFactory;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.User;
import maquette.util.TestSetup;

public final class TestContext {

    private static final Logger LOG = LoggerFactory.getLogger(TestContext.class);

    private TestSetup setup;

    private Map<String, User> users;

    private Map<String, ResourcePath> knownDatasets;

    private Map<String, Object> variables;

    public TestContext() {
        this.users = Maps.newHashMap();
        this.knownDatasets = Maps.newHashMap();
        this.variables = Maps.newHashMap();
    }

    public void addKnownDataset(ResourcePath dataset) {
        knownDatasets.put(dataset.getName().toString(), dataset);
        LOG.debug(String.format("Added dataset '%s' to list of known datasets", dataset));
    }

    public ResourcePath getKnownDataset(String name) {
        return Optional
            .ofNullable(knownDatasets.get(name))
            .orElseThrow(() -> new RuntimeException(String.format(
                "No known dataset %s. Did the scenario setup this dataset before?", name)));
    }

    public TestSetup getSetup() {
        return setup;
    }

    public User getUser(String user) {
        return users.get(user);
    }

    public <T> T getVariable(String key, Class<T> type) {
        return getVariable(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getVariable(String key) {
        Object value = variables.get(key);

        if (value == null) {
            throw new RuntimeException(String.format("No variable '%s' stored in previous steps", key));
        } else {
            return (T) value;
        }
    }

    public void setVariable(String key, Object value) {
        variables.put(key, value);
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
