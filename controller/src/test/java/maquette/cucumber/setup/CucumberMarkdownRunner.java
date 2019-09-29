package maquette.cucumber.setup;

import org.junit.runners.model.InitializationError;

import cucumber.api.junit.Cucumber;

public class CucumberMarkdownRunner extends Cucumber {

    static {
        CucumberExtractFeatures.main();
    }

    /**
     * Constructor called by JUnit.
     *
     * @param clazz
     *     the class with the @RunWith annotation.
     * @throws InitializationError
     *     if there is another problem
     */
    public CucumberMarkdownRunner(Class clazz) throws InitializationError {
        super(clazz);
    }
}
