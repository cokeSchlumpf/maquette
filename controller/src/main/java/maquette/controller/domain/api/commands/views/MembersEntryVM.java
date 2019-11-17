package maquette.controller.domain.api.commands.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.values.dataset.DatasetMember;
import maquette.controller.domain.values.project.ProjectGrant;

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

    public static MembersEntryVM apply(ProjectGrant grant, OutputFormat outputFormat) {
        return MembersEntryVM.apply(
            outputFormat.format(grant.getAuthorization().getAuthorization()),
            outputFormat.format(grant.getPrivilege().name),
            outputFormat.format(grant.getAuthorization().getBy()),
            outputFormat.format(grant.getAuthorization().getAt()));
    }

    public static MembersEntryVM apply(DatasetMember grant, OutputFormat outputFormat) {
        return MembersEntryVM.apply(
            outputFormat.format(grant.getAuthorization().getAuthorization()),
            outputFormat.format(grant.getPrivilege().name),
            outputFormat.format(grant.getAuthorization().getBy()),
            outputFormat.format(grant.getAuthorization().getAt()));
    }

}
