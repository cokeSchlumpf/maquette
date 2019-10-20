package maquette.cucumber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.cucumber.datatable.DataTable;
import lombok.AllArgsConstructor;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.commands.EAuthorizationType;
import maquette.controller.adapters.cli.commands.datasets.ChangeDatasetDescriptionCmd;
import maquette.controller.adapters.cli.commands.datasets.ChangeDatasetPrivacyCmd;
import maquette.controller.adapters.cli.commands.datasets.GrantDatasetAccessCmd;
import maquette.controller.adapters.cli.commands.datasets.ListDatasetVersionsCmd;
import maquette.controller.adapters.cli.commands.datasets.PrintDatasetDetailsCmd;
import maquette.controller.adapters.cli.commands.datasets.RevokeDatasetAccessCmd;
import maquette.controller.adapters.cli.commands.datasets.CreateDatasetCmd;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.records.Records;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor
public final class DatasetSteps {

    private static final Logger LOG = LoggerFactory.getLogger(DatasetSteps.class);

    private final TestContext ctx;

    @Given("{string} becomes a consumer of dataset {string}")
    public void becomes_a_consumer_of_dataset(String username, String datasetName) throws ExecutionException, InterruptedException {
        ResourcePath dataset = ctx.getKnownDataset(datasetName);

        GrantDatasetAccessCmd
            .apply(dataset.getProject(), dataset.getName(), EAuthorizationType.USER, DatasetPrivilege.CONSUMER, username)
            .run(ctx.getSetup().getAdminUser(), ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();
    }

    @Given("{string} becomes a producer of dataset {string}")
    public void becomes_a_producer_of_dataset(String username, String datasetName) throws ExecutionException, InterruptedException {
        ResourcePath dataset = ctx.getKnownDataset(datasetName);

        GrantDatasetAccessCmd
            .apply(dataset.getProject(), dataset.getName(), EAuthorizationType.USER, DatasetPrivilege.PRODUCER, username)
            .run(ctx.getSetup().getAdminUser(), ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();
    }

    @Then("{string} cannot see details of the dataset")
    public void cannot_see_details_of_the_dataset(String username) throws ExecutionException, InterruptedException {
        String datasetName = ctx.getVariable("dataset", String.class);
        User user = ctx.getUser(username);
        ResourcePath dataset = ctx.getKnownDataset(datasetName);

        assertThatThrownBy(() -> PrintDatasetDetailsCmd
            .apply(dataset.getProject(), dataset.getName())
            .run(user, ctx.getSetup().getApp())
            .toCompletableFuture()
            .get()).hasMessageContaining("not authorized");
    }

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

        CreateDatasetCmd
            .apply(project, dataset, false)
            .run(user, ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();
    }

    @Given("consumer access for dataset {string} is revoked from {string}")
    public void consumer_access_for_dataset_is_revoked_from(String datasetName, String username)
        throws ExecutionException, InterruptedException {

        ResourcePath dataset = ctx.getKnownDataset(datasetName);

        RevokeDatasetAccessCmd
            .apply(dataset.getProject(), dataset.getName(), EAuthorizationType.USER, DatasetPrivilege.CONSUMER, username)
            .run(ctx.getSetup().getAdminUser(), ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();
    }

    @Given("dataset {string} is set to be private")
    public void dataset_is_set_to_be_private(String datasetName) throws ExecutionException, InterruptedException {
        ResourcePath dataset = ctx.getKnownDataset(datasetName);
        User user = ctx.getSetup().getAdminUser();

        ChangeDatasetPrivacyCmd
            .apply(dataset.getProject(), dataset.getName(), true)
            .run(user, ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();

        ctx.setVariable("dataset", datasetName);
    }

    @Then("dataset {string} of project {string} should be owned by role {string}")
    public void dataset_of_project_should_be_owned_by_role(String datasetName, String projectName, String role)
        throws ExecutionException, InterruptedException {
        ResourceName project = ResourceName.apply(projectName);
        ResourceName dataset = ResourceName.apply(datasetName);

        CommandResult result = PrintDatasetDetailsCmd
            .apply(project, dataset)
            .run(ctx.getSetup().getAdminUser(), ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();

        LOG.debug(String.format("$ datasets details\n%s", result.getOutput()));

        assertThat(
            Lists.newArrayList(result.getOutput().split("\n"))
                 .stream()
                 .anyMatch(line -> line.contains("OWNER") && line.contains(role))).isTrue();
    }

    @Then("{string} should be able to see details of dataset {string}")
    public void should_be_able_to_see_details_of_dataset(String username, String datasetName)
        throws ExecutionException, InterruptedException {
        User user = ctx.getUser(username);
        ResourcePath dataset = ctx.getKnownDataset(datasetName);

        CommandResult result = PrintDatasetDetailsCmd
            .apply(dataset.getProject(), dataset.getName())
            .run(user, ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();

        LOG.debug(String.format("& datasets details\n%s", result.getOutput()));

        assertThat(result.getOutput()).contains("PROPERTIES");
    }

    @Then("{string} should find {string} when listing his own datasets")
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

    @Then("the dataset details should contain this description.")
    public void the_dataset_details_should_contain_this_description() throws ExecutionException, InterruptedException {
        String datasetName = ctx.getVariable("dataset", String.class);
        String description = ctx.getVariable("description", String.class);
        ResourcePath dataset = ctx.getKnownDataset(datasetName);

        CommandResult result = PrintDatasetDetailsCmd
            .apply(dataset.getProject(), dataset.getName())
            .run(ctx.getSetup().getAdminUser(), ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();

        LOG.debug(String.format("$ datasets details\n%s", result.getOutput()));

        assertThat(result.getOutput()).contains(description);
    }

    @Given("user {string} updates the description of dataset {string} to")
    public void user_updates_the_description_of_dataset_to(String username, String datasetName, String description)
        throws ExecutionException, InterruptedException {
        User user = ctx.getUser(username);
        ResourcePath dataset = ctx.getKnownDataset(datasetName);

        ChangeDatasetDescriptionCmd
            .apply(dataset.getProject(), dataset.getName(), Markdown.apply(description))
            .run(user, ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();

        ctx.setVariable("dataset", datasetName);
        ctx.setVariable("description", description);
    }

    @Then("we expect {int} existing version\\(s) in the dataset")
    public void we_expect_existing_version_in_the_dataset(int count) throws ExecutionException, InterruptedException {
        String datasetName = ctx.getVariable("dataset", String.class);
        ResourcePath dataset = ctx.getKnownDataset(datasetName);

        CommandResult result = PrintDatasetDetailsCmd
            .apply(dataset.getProject(), dataset.getName())
            .run(ctx.getSetup().getAdminUser(), ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();

        LOG.debug(String.format("$ datasets details\n%s", result.getOutput()));

        assertThat(
            Lists
                .newArrayList(result.getOutput())
                .stream()
                .anyMatch(s -> s.contains("VERSION") && s.contains(String.valueOf(count)))).isTrue();
    }

    @Then("we expect that version {string} exists in the dataset")
    public void we_expect_that_version_exists_in_the_dataset(String version) throws ExecutionException, InterruptedException {
        String datasetName = ctx.getVariable("dataset", String.class);
        ResourcePath dataset = ctx.getKnownDataset(datasetName);

        CommandResult result = ListDatasetVersionsCmd
            .apply(dataset.getProject(), dataset.getName())
            .run(ctx.getSetup().getAdminUser(), ctx.getSetup().getApp())
            .toCompletableFuture()
            .get();

        LOG.debug(String.format("$ datasets details\n%s", result.getOutput()));

        assertThat(
            Lists
                .newArrayList(result.getOutput())
                .stream()
                .anyMatch(s -> s.contains(version))).isTrue();
    }

    @Then("we expect that version {string} contains {int} tuples")
    public void we_expect_that_version_contains_tuples(String version, Integer count)
        throws ExecutionException, InterruptedException {
        String datasetName = ctx.getVariable("dataset", String.class);
        ResourcePath dataset = ctx.getKnownDataset(datasetName);

        Records records = ctx
            .getSetup()
            .getApp()
            .datasets()
            .getData(ctx.getSetup().getAdminUser(), dataset, VersionTag.apply(version))
            .toCompletableFuture()
            .get();

        assertThat(records.getRecords()).hasSize(count);
    }

    @Given("we have the following datasets in project {string}")
    public void we_have_the_following_datasets_in_project(String projectName, DataTable dataTable)
        throws ExecutionException, InterruptedException {

        ResourceName project = ResourceName.apply(projectName);
        List<List<String>> data = Lists.newArrayList(dataTable.asLists());
        data.remove(0);

        for (List<String> ds : data) {
            ResourceName dataset = ResourceName.apply(ds.get(0));

            CreateDatasetCmd
                .apply(project, dataset, ds.get(1).equals("yes"))
                .run(ctx.getSetup().getAdminUser(), ctx.getSetup().getApp())
                .toCompletableFuture()
                .get();

            ctx.addKnownDataset(ResourcePath.apply(project, dataset));
        }
    }

}
