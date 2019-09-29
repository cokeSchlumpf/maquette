package maquette.cucumber;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import maquette.util.TestSetup;

public class HelloWorldSteps {

    private TestSetup setup;

    @Given("today is Sunday")
    public void today_is_Sunday() {

    }

    @When("I ask whether it's Friday yet")
    public void i_ask_whether_it_s_Friday_yet() {

    }

    @Then("^I should be told \"([^\"]*)\"$")
    public void i_should_be_told(String string) {

    }

}
