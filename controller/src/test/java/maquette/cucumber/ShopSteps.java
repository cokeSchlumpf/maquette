package maquette.cucumber;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.java.en.Then;
import lombok.AllArgsConstructor;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.commands.shop.FindDatasetsCmd;
import maquette.controller.adapters.cli.commands.shop.FindProjectsCmd;
import maquette.controller.adapters.cli.commands.shop.ListDatasetsCmd;
import maquette.controller.adapters.cli.commands.shop.ListProjectsCmd;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor
public final class ShopSteps {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(DatasetSteps.class);

    private final TestContext ctx;

    @Then("{string} should be able to see dataset {string} when browsing available datasets")
    public void should_be_able_to_see_dataset_when_browsing_available_datasets(String username, String datasetName)
        throws ExecutionException, InterruptedException {

        User user = ctx.getUser(username);

        CommandResult result = FindDatasetsCmd
            .apply()
            .run(user, ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();

        LOG.debug(String.format("$ shop datasets list\n\n%s", result.getOutput()));

        assertThat(result.getOutput()).contains(datasetName);
    }

    @Then("{string} should find the project when searching for projects")
    public void should_find_the_project_when_searching_for_projects(String userName)
        throws ExecutionException, InterruptedException {

        User user = ctx.getUser(userName);

        CommandResult result = FindProjectsCmd
            .apply()
            .run(user, ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();

        LOG.debug(String.format("$ shop projects find\n\n%s", result.getOutput()));

        assertThat(result.getOutput()).contains(ctx.getVariable("project", String.class));
    }

    @Then("{string} should not find the project when searching for projects")
    public void should_not_find_the_project_when_searching_for_projects(String userName)
        throws ExecutionException, InterruptedException {

        User user = ctx.getUser(userName);

        CommandResult result = FindProjectsCmd
            .apply()
            .run(user, ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();

        LOG.debug(String.format("$ shop projects find\n\n%s", result.getOutput()));

        assertThat(result.getOutput()).doesNotContain(ctx.getVariable("project", String.class));
    }

    @Then("{string} should find the project when listing (his|her) own projects")
    public void should_find_the_project_when_listing_his_own_projects(String userName)
        throws ExecutionException, InterruptedException {
        User user = ctx.getUser(userName);

        CommandResult result = ListProjectsCmd
            .apply()
            .run(user, ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();

        LOG.debug(String.format("$ shop projects list\n\n%s", result.getOutput()));

        assertThat(result.getOutput()).contains(ctx.getVariable("project", String.class));
    }

    @Then("{string} should not be able to see dataset {string} when browsing available datasets")
    public void should_not_be_able_to_see_dataset_when_browsing_available_datasets(String username, String datasetName)
        throws ExecutionException, InterruptedException {

        User user = ctx.getUser(username);

        CommandResult result = FindDatasetsCmd
            .apply()
            .run(user, ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();

        LOG.debug(String.format("$ shop datasets list\n\n%s", result.getOutput()));

        assertThat(result.getOutput()).doesNotContain(datasetName);
    }

}
