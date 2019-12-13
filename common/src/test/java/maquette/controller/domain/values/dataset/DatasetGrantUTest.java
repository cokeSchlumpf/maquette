package maquette.controller.domain.values.dataset;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.Test;

import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.UserAuthorization;
import maquette.controller.domain.values.iam.UserId;

public class DatasetGrantUTest {

    @Test
    public void test() {
        UID uid = UID.apply();
        UserAuthorization user = UserAuthorization.apply("user");
        DatasetPrivilege privilege = DatasetPrivilege.CONSUMER;
        UserId executor = UserId.apply("egon");

        DatasetGrant grant = DatasetGrant.createApproved(
            uid, user, privilege,
            executor, Instant.now(), Markdown.lorem());

        assertThat(grant.getRequest())
            .as("As it was immediately generated as approved, there should not be a request")
            .isNotPresent();

        assertThat(grant.getRequestResponse())
            .as("The request response should be set and it should be approved")
            .isPresent()
            .satisfies(r -> assertThat(r.isPresent()).isTrue());

        System.out.println(grant.getRequestResponse().get());
    }

}