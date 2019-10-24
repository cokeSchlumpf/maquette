package maquette.controller.application.commands.views;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.application.commands.OutputFormat;
import maquette.controller.application.commands.ViewModel;
import maquette.controller.domain.values.project.ProjectDetails;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectsVM implements ViewModel {

    private static final String COUNT = "count";
    private static final String PROJECTS = "projects";

    @JsonProperty(COUNT)
    private final int count;

    @JsonProperty(PROJECTS)
    private final List<ProjectCardVM> projects;

    @JsonCreator
    public static ProjectsVM apply(
        @JsonProperty(COUNT) int count,
        @JsonProperty(PROJECTS) List<ProjectCardVM> projects) {

        return new ProjectsVM(count, ImmutableList.copyOf(projects));
    }

    public static ProjectsVM apply(Collection<ProjectDetails> projects, OutputFormat out) {
        List<ProjectCardVM> cards = projects
            .stream()
            .sorted(Comparator.comparing(p -> p.getName().getValue()))
            .map(details -> ProjectCardVM.apply(details, out))
            .collect(Collectors.toList());

        return apply(cards.size(), cards);
    }

}
