package maquette.controller.domain.api.commands;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import maquette.controller.domain.api.Command;
import maquette.controller.domain.api.commands.shop.ListProjectsCmd;
import maquette.controller.domain.util.databind.ObjectMapperFactory;

public class ListProjectsCmdTest {

    @Test
    public void testJson() throws IOException {
        ObjectMapper om = ObjectMapperFactory.apply().create(true);
        ListProjectsCmd cmd = ListProjectsCmd.apply();

        String json = om.writeValueAsString(cmd);
        Command c = om.readValue(json, Command.class);

        System.out.println(json);
        assertThat(c).isEqualTo(cmd);
    }

}