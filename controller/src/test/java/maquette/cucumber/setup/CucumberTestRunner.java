package maquette.cucumber.setup;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;

@RunWith(CucumberMarkdownRunner.class)
@CucumberOptions(features = { "src/test/resources" }, plugin = "pretty", glue = "maquette.cucumber")
public class CucumberTestRunner {

}
