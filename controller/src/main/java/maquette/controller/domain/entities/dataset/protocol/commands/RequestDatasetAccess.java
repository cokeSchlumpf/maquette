package maquette.controller.domain.entities.dataset.protocol.commands;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestDatasetAccess implements DatasetMessage {

    private static final String EXECUTOR = "executor";
    private static final String GRANT = "grant";
    private static final String GRANT_FOR = "grant-for";
    private static final String JUSTIFICATION = "justification";

    @JsonProperty(EXECUTOR)
    private final User executor;

    @JsonProperty(JUSTIFICATION)
    private final String justification;

    @JsonProperty(GRANT)
    private final DatasetPrivilege grant;

    @JsonProperty(GRANT_FOR)
    private final Authorization grantFor;

}
