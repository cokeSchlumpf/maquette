package maquette.controller.domain.values.project;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectDetails {

    private final ProjectProperties properties;

}
