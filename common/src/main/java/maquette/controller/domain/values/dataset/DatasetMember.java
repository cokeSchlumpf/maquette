package maquette.controller.domain.values.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.iam.GrantedAuthorization;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasetMember {

    private final GrantedAuthorization authorization;

    private final DatasetPrivilege privilege;

    @JsonCreator
    public static DatasetMember apply(
        @JsonProperty("authorization") GrantedAuthorization authorization,
        @JsonProperty("privilege") DatasetPrivilege privilege) {

        return new DatasetMember(authorization, privilege);
    }

}
