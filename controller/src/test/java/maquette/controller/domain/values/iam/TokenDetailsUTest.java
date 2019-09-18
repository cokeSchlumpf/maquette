package maquette.controller.domain.values.iam;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Instant;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import maquette.controller.domain.util.databind.ObjectMapperFactory;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.UID;

public class TokenDetailsUTest {

    @Test
    public void testJson() throws IOException {
        ObjectMapper om = ObjectMapperFactory.apply().create(true);
        TokenDetails tokenDetails = TokenDetails.apply(
            ResourceName.apply("foo"), UID.apply(), UserId.apply("foo"),
            Instant.now(), UserId.apply("egon"), Instant.now(), UserId.apply("egon"));

        String json = om.writeValueAsString(tokenDetails);
        TokenDetails tokenDetails$decoded = om.readValue(json, TokenDetails.class);
        System.out.println(json);

        assertThat(tokenDetails$decoded).isEqualTo(tokenDetails);
    }

    @Test
    public void testTokenAuthorization() throws IOException {
        ObjectMapper om = ObjectMapperFactory.apply().create(true);
        TokenAuthorization auth = TokenAuthorization.apply(UID.apply());

        String json = om.writeValueAsString(auth);
        Authorization auth$decoded = om.readValue(json, Authorization.class);

        assertThat(auth$decoded).isEqualTo(auth);
    }

}