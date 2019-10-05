package maquette.cucumber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecordBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.cucumber.datatable.DataTable;
import lombok.AllArgsConstructor;
import maquette.controller.domain.util.databind.ObjectMapperFactory;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.records.Records;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.User;
import maquette.util.CountryTestData;

@AllArgsConstructor
public final class DataSteps {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(DatasetSteps.class);

    private final TestContext ctx;

    @Given("dataset {string} contains {int} versions")
    public void dataset_contains_versions(String datasetName, int count) throws ExecutionException, InterruptedException {
        User user = ctx.getSetup().getAdminUser();
        ResourcePath dataset = ctx.getKnownDataset(datasetName);

        Records records = Records.fromRecords(CountryTestData.getRecords());

        for (int i = 0; i < count; i++) {
            ctx
                .getSetup()
                .getApp()
                .datasets()
                .putData(user, dataset, records.getSource(), "some message " + i)
                .toCompletableFuture()
                .get();
        }

        ctx.setVariable("dataset", datasetName);
        ctx.setVariable("records", records);
    }

    @Then("{string} produces data to dataset {string} we( still) expect an exception")
    public void produces_data_to_dataset_we_expect_an_exception(String username, String datasetName) {
        User user = ctx.getUser(username);
        ResourcePath dataset = ctx.getKnownDataset(datasetName);

        assertThatThrownBy(() -> ctx
            .getSetup()
            .getApp()
            .datasets()
            .putData(user, dataset, Records.fromRecords(CountryTestData.getRecords()).getSource(), "some data")
            .toCompletableFuture()
            .get()).hasMessageContaining("not authorized");
    }

    @Then("{string} receives data from dataset {string} we( still) expect an exception")
    public void reads_data_from_dataset_we_expect_an_exception(String username, String datasetName) {
        User user = ctx.getUser(username);
        ResourcePath dataset = ctx.getKnownDataset(datasetName);

        assertThatThrownBy(() -> ctx
            .getSetup()
            .getApp()
            .datasets()
            .getData(user, dataset)
            .toCompletableFuture()
            .get()).hasMessageContaining("not authorized");
    }

    @Then("receiving data from version {string} we expect an exception")
    public void receiving_data_from_version_we_expect_an_exception(String version) {
        String datasetName = ctx.getVariable("dataset", String.class);
        ResourcePath dataset = ctx.getKnownDataset(datasetName);
        User user = ctx.getSetup().getAdminUser();

        assertThatThrownBy(() -> ctx
            .getSetup()
            .getApp()
            .datasets()
            .getData(user, dataset, VersionTag.apply(version))
            .toCompletableFuture()
            .get()).hasMessageContaining("does not exist in dataset");
    }

    @Then("we expect that we received {int} tuples")
    public void we_expect_that_we_received_tuples(Integer count) {
        Records records = ctx.getVariable("records", Records.class);
        assertThat(records.getRecords()).hasSize(count);
    }

    @Then("we expect that we received at least {int} tuple\\(s)")
    public void we_expect_that_we_received_at_least_tuple_s(int count) {
        Records records = ctx.getVariable("records", Records.class);
        assertThat(records.getRecords().size()).isGreaterThan(count);
    }

    @Given("we have a dataset of the following schema:")
    public void we_have_a_dataset_of_the_following_schema(String schemaJson) throws IOException {
        ObjectMapper om = ObjectMapperFactory.apply().create(true);
        Schema schema = om.readValue(schemaJson, Schema.class);

        ctx.setVariable("schema", schema);
    }

    @Given("we have the following data:")
    public void we_have_the_following_data(DataTable dataTable) {
        Schema schema = ctx.getVariable("schema", Schema.class);
        List<List<String>> data = Lists.newArrayList(dataTable.asLists());
        List<String> fields = data.get(0);
        List<GenericData.Record> records = Lists.newArrayList();

        data.remove(0);

        for (List<String> tuple : data) {
            GenericRecordBuilder builder = new GenericRecordBuilder(schema);

            for (int i = 0; i < fields.size(); i++) {
                String field = fields.get(i);
                String value = tuple.get(i);

                builder.set(field, value);
            }

            records.add(builder.build());
        }

        ctx.setVariable("records", Records.fromRecords(records));
    }

    @Given("{string} pushes this data to dataset {string}")
    public void pushes_this_data_to_dataset(String username, String datasetName) throws ExecutionException, InterruptedException {
        User user = ctx.getUser(username);
        ResourcePath knownDataset = ctx.getKnownDataset(datasetName);
        Records records = ctx.getVariable("records", Records.class);

        ctx
            .getSetup()
            .getApp()
            .datasets()
            .putData(user, knownDataset, records.getSource(), "some message")
            .toCompletableFuture()
            .get();

        ctx.setVariable("dataset", datasetName);
    }

    @Given("we push this data( again) to dataset {string}")
    public void we_push_this_data_to_dataset(String datasetName) throws ExecutionException, InterruptedException {
        AuthenticatedUser user = ctx.getSetup().getAdminUser();
        ResourcePath knownDataset = ctx.getKnownDataset(datasetName);
        Records records = ctx.getVariable("records", Records.class);

        ctx
            .getSetup()
            .getApp()
            .datasets()
            .putData(user, knownDataset, records.getSource(), "some message")
            .toCompletableFuture()
            .get();

        ctx.setVariable("dataset", datasetName);
    }

    @Given("we read data from dataset {string}")
    public void we_read_data_from_dataset(String datasetName) throws ExecutionException, InterruptedException {
        ResourcePath dataset = ctx.getKnownDataset(datasetName);
        User user = ctx.getSetup().getAdminUser();

        Records records = ctx
            .getSetup()
            .getApp()
            .datasets()
            .getData(user, dataset)
            .toCompletableFuture()
            .get();

        ctx.setVariable("dataset", datasetName);
        ctx.setVariable("records", records);
    }

    @Given("we read data from dataset {string} with version {string}")
    public void we_read_data_from_dataset_with_version(String datasetName, String version)
        throws ExecutionException, InterruptedException {

        ResourcePath dataset = ctx.getKnownDataset(datasetName);
        User user = ctx.getSetup().getAdminUser();

        Records records = ctx
            .getSetup()
            .getApp()
            .datasets()
            .getData(user, dataset, VersionTag.apply(version))
            .toCompletableFuture()
            .get();

        ctx.setVariable("dataset", datasetName);
        ctx.setVariable("records", records);
    }

    @Given("{string} reads data from dataset {string}")
    public void reads_data_from_dataset(String username, String datasetName) throws ExecutionException, InterruptedException {
        ResourcePath dataset = ctx.getKnownDataset(datasetName);
        User user = ctx.getUser(username);

        Records records = ctx
            .getSetup()
            .getApp()
            .datasets()
            .getData(user, dataset)
            .toCompletableFuture()
            .get();

        ctx.setVariable("dataset", datasetName);
        ctx.setVariable("records", records);
    }

}
