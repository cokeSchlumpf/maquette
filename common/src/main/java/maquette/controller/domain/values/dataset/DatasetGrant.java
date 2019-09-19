package maquette.controller.domain.values.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.iam.GrantedAuthorization;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasetGrant {

    private final GrantedAuthorization authorization;

    private final DatasetPrivilege privilege;

    @JsonCreator
    public static DatasetGrant apply(
        @JsonProperty("authorization") GrantedAuthorization authorization,
        @JsonProperty("privilege") DatasetPrivilege privilege) {

        return new DatasetGrant(authorization, privilege);
    }

}
