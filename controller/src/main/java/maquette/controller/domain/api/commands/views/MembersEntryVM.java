package maquette.controller.domain.api.commands.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MembersEntryVM {

    private static final String GRANTED_TO = "granted-to";
    private static final String PRIVILEGE = "privilege";
    private static final String GRANTED_BY = "granted-by";
    private static final String GRANTED_AT = "granted-at";

    @JsonProperty(GRANTED_TO)
    private final String grantedTo;

    @JsonProperty(PRIVILEGE)
    private final String privilege;

    @JsonProperty(GRANTED_BY)
    private final String grantedBy;

    @JsonProperty(GRANTED_AT)
    private final String grantedAt;

    @JsonCreator
    public static MembersEntryVM apply(
        @JsonProperty(GRANTED_TO) String grantedTo,
        @JsonProperty(PRIVILEGE) String privilege,
        @JsonProperty(GRANTED_BY) String grantedBy,
        @JsonProperty(GRANTED_AT) String grantedAt) {

        return new MembersEntryVM(grantedTo, privilege, grantedBy, grantedAt);
    }

}
