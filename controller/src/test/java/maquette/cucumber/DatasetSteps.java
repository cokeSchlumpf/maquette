package maquette.cucumber;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.java.en.Given;
import lombok.AllArgsConstructor;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.commands.projects.CreateProjectDatasetCmd;
import maquette.controller.adapters.cli.commands.users.CreateUserDatasetCmd;
import maquette.controller.adapters.cli.commands.users.ListUserDatasetsCmd;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor
public final class DatasetSteps {

    private static final Logger LOG = LoggerFactory.getLogger(DatasetSteps.class);

    private final TestContext ctx;

    @Given("{string} creates a dataset called {string}")
    public void creates_a_dataset_called(String username, String dataset) throws ExecutionException, InterruptedException {
        User user = ctx.getUser(username);

        CommandResult result = CreateUserDatasetCmd
            .apply(ResourceName.apply(dataset), false)
            .run(user, ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();

        LOG.debug("$ user datasets create" + result.getOutput());
    }

    @Given("{string} creates a dataset called {string} in project {string}")
    public void creates_a_dataset_called_in_project(String username, String datasetName, String projectName)
        throws ExecutionException, InterruptedException {

        User user = ctx.getUser(username);
        ResourceName project = ResourceName.apply(projectName);
        ResourceName dataset = ResourceName.apply(datasetName);

        CreateProjectDatasetCmd
            .apply(project, dataset, false)
            .run(user, ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();
    }

    @Given("{string} should find {string} when listing his own datasets")
    public void should_find_when_listing_his_own_datasets(String username, String datasetName)
        throws ExecutionException, InterruptedException {

        User user = ctx.getUser(username);

        CommandResult result = ListUserDatasetsCmd
            .apply()
            .run(user, ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();

        LOG.debug(String.format("$ user datasets\n%s", result.getOutput()));

        assertThat(result.getOutput()).contains(datasetName);
    }

    @Given("{string} should find {string} when listing her own datasets")
    public void should_find_when_listing_her_own_datasets(String username, String password)
        throws ExecutionException, InterruptedException {

        should_find_when_listing_his_own_datasets(username, password);
    }

}
