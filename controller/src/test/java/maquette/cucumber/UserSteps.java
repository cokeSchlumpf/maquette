package maquette.cucumber;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import cucumber.api.java.en.Then;
import lombok.AllArgsConstructor;
import maquette.controller.domain.api.CommandResult;
import maquette.controller.domain.api.OutputFormat;
import maquette.controller.domain.api.commands.users.ShowNotificationsCmd;
import maquette.controller.domain.api.commands.users.ShowUserCmd;
import maquette.controller.domain.util.databind.ObjectMapperFactory;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor
public final class UserSteps {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(DatasetSteps.class);

    private static final ObjectMapper OM = ObjectMapperFactory.apply().create(true);

    private final TestContext ctx;

    @Then("{string} has received a notification to review the request")
    public void has_received_a_notification_to_review_the_request(String username) throws ExecutionException, InterruptedException {
        User user = ctx.getUser(username);
        String requestId = ctx.getVariable("requestId");

        CommandResult result = ShowNotificationsCmd
            .apply()
            .run(user, ctx.getSetup().getApp(), OutputFormat.apply())
            .toCompletableFuture()
            .get()
            .toCommandResult(OM);

        LOG.debug(String.format("$ user notifications\n\n%s\n\n", result.getOutput()));

        assertThat(result.getOutput()).contains(requestId);
    }

    @Then("{string} receives a notification that her request was approved")
    public void receives_a_notification_that_her_request_was_approved(String username)
        throws ExecutionException, InterruptedException {
        User user = ctx.getUser(username);

        CommandResult result = ShowNotificationsCmd
            .apply()
            .run(user, ctx.getSetup().getApp(), OutputFormat.apply())
            .toCompletableFuture()
            .get()
            .toCommandResult(OM);

        LOG.debug(String.format("$ user notifications\n\n%s\n\n", result.getOutput()));

        assertThat(result.getOutput())
            .contains("approved")
            .contains("request");
    }

    @Then("{string} sees the request when viewing her personal profile info")
    public void sees_the_request_when_viewing_her_personal_profile_info(String username)
        throws ExecutionException, InterruptedException {
        User user = ctx.getUser(username);
        String requestId = ctx.getVariable("requestId");

        CommandResult result = ShowUserCmd
            .apply()
            .run(user, ctx.getSetup().getApp(), OutputFormat.apply())
            .toCompletableFuture()
            .get()
            .toCommandResult(OM);

        LOG.debug(String.format("$ user show\n\n%s\n\n", result.getOutput()));

        assertThat(result.getOutput()).contains(requestId);
    }

}
