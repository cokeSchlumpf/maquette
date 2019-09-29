package maquette.cucumber;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class HelloWorldSteps {

    private TestContext ctx;

    @Given("today is Sunday")
    public void today_is_Sunday() {
        System.out.println(ctx.getSetup().getDefaultUser());
    }

    @When("I ask whether it's Friday yet")
    public void i_ask_whether_it_s_Friday_yet() {

    }

    @Then("^I should be told \"([^\"]*)\"$")
    public void i_should_be_told(String string) {

    }

}
