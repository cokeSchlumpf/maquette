package maquette.controller.domain.values.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Instant;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import maquette.controller.domain.entities.dataset.protocol.events.CreatedDataset;
import maquette.controller.domain.util.databind.ObjectMapperFactory;
import maquette.controller.domain.values.core.records.Records;
import maquette.controller.domain.values.iam.UserId;
import maquette.util.CountryTestData;

public class RecordsUTest {

    @Test
    public void test() throws IOException {
        Records records = Records.fromRecords(CountryTestData.getRecords());
        Records encoded = Records.fromByteStrings(records.getBytes());

        assertThat(encoded.getRecords()).hasSize(records.getRecords().size());
        assertThat(encoded.getSchema()).isEqualTo(records.getSchema());

        ObjectMapper om = ObjectMapperFactory.apply().create(true);
        String json = om.writeValueAsString(records);

        Records recordsDecoded = om.readValue(json, Records.class);

        assertThat(recordsDecoded.getRecords()).hasSize(records.getRecords().size());
        assertThat(recordsDecoded.getSchema()).isEqualTo(records.getSchema());

        assertThat(Records.empty().getBytes().size()).isGreaterThan(0);
    }

}
