package maquette.controller.domain.api.commands.views;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.ViewModel;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectVM implements ViewModel {

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String PRIVATE = "private";
    private static final String OWNER = "owner";
    private static final String MEMBERS = "members";

    @JsonProperty(NAME)
    private final String name;

    @JsonProperty(DESCRIPTION)
    private final String description;

    @JsonProperty(PRIVATE)
    private boolean isPrivate;

    @JsonProperty(OWNER)
    private final AuthorizationVM owner;

    @JsonProperty(MEMBERS)
    private final List<MembersEntryVM> members;

    @JsonCreator
    public static ProjectVM apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(PRIVATE) boolean isPrivate,
        @JsonProperty(OWNER) AuthorizationVM owner,
        @JsonProperty(MEMBERS) List<MembersEntryVM> members) {

        return new ProjectVM(name, description, isPrivate, owner, ImmutableList.copyOf(members));
    }

}
