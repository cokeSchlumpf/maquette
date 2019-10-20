package maquette.controller.domain.values.deprecatedproject;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;

@Value
@Wither
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectProperties {

    private static final String NAME = "name";
    private static final String IS_PRIVATE = "private";
    private static final String DESCRIPTION = "description";

    @JsonProperty(NAME)
    private final ResourceName name;

    @JsonProperty(IS_PRIVATE)
    private final boolean isPrivate;

    @JsonProperty(DESCRIPTION)
    private final Markdown description;

    @JsonCreator
    public static ProjectProperties apply(
        @JsonProperty(NAME) ResourceName name,
        @JsonProperty(IS_PRIVATE) boolean isPrivate,
        @JsonProperty(DESCRIPTION) Markdown description) {

        return new ProjectProperties(name, isPrivate, description);
    }

    public static ProjectProperties apply(ResourceName name, boolean isPrivate) {
        return new ProjectProperties(name, isPrivate, null);
    }

    public Optional<Markdown> getDescription() {
        return Optional.ofNullable(description);
    }

}
