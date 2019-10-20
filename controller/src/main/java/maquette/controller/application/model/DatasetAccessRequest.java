package maquette.controller.application.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.iam.Authorization;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasetAccessRequest {

    private final DatasetPrivilege privilege;

    private final Authorization authorization;

    @JsonCreator
    public static DatasetAccessRequest apply(
        @JsonProperty("privilege") DatasetPrivilege privilege,
        @JsonProperty("authorization") Authorization authorization) {

        return new DatasetAccessRequest(privilege, authorization);
    }

}
