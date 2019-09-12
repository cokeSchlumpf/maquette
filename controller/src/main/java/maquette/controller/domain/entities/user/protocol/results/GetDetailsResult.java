package maquette.controller.domain.entities.user.protocol.results;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.user.protocol.Message;
import maquette.controller.domain.values.iam.UserDetails;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetDetailsResult implements Message {

    private final UserDetails details;

    public static GetDetailsResult apply(
        @JsonProperty("details") UserDetails details) {

        return new GetDetailsResult(details);
    }

}
