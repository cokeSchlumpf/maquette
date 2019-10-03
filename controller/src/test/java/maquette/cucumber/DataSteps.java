package maquette.cucumber;

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
import io.cucumber.datatable.DataTable;
import lombok.AllArgsConstructor;
import maquette.controller.domain.util.databind.ObjectMapperFactory;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.records.Records;
import maquette.controller.domain.values.iam.AuthenticatedUser;

@AllArgsConstructor
public final class DataSteps {

    private static final Logger LOG = LoggerFactory.getLogger(DatasetSteps.class);

    private final TestContext ctx;

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

}
