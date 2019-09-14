package maquette.controller.adapters.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import maquette.controller.domain.util.databind.ObjectMapperFactory;

public class DataTableUTest {

    @Test
    public void test() {
        DataTable dt = DataTable
            .apply("Team", "Points", "Rank")
            .withRow("VFC Plauen", "67", "1")
            .withRow(Lists.newArrayList("FC Bayern", "66", "2"))
            .withRow("Fortuna Düsseldorf", "34", "3");

        String csv = dt.toCSV();

        assertThat(csv)
            .hasLineCount(4)
            .contains("Team;Points;Rank")
            .contains("VFC Plauen;67;1");

        System.out.println(csv);

        assertThat(DataTable.fromCSV(csv)).isEqualTo(dt);
    }

    @Test
    public void testJson() throws IOException {
        ObjectMapper om = ObjectMapperFactory.apply().create(true);
        DataTable dt = DataTable
            .apply("Team", "Points", "Rank")
            .withRow("VFC Plauen", "67", "1")
            .withRow(Lists.newArrayList("FC Bayern", "66", "2"))
            .withRow("Fortuna Düsseldorf", "34", "3");

        String json = om.writeValueAsString(dt);

        System.out.println(json);

        DataTable dt2 = om.readValue(json, DataTable.class);

        assertThat(dt2).isEqualTo(dt);
    }

}