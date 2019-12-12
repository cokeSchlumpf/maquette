package maquette.controller.domain.api.views;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.CommandResult;
import maquette.controller.domain.api.DataTable;
import maquette.controller.domain.api.DataTables;
import maquette.controller.domain.api.OutputFormat;
import maquette.controller.domain.api.ViewModel;
import maquette.controller.domain.values.iam.User;
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

    public static ProjectsVM apply(
        Collection<ProjectDetails> projects,
        User executor,
        OutputFormat out) {

        List<ProjectCardVM> cards = projects
            .stream()
            .sorted(Comparator.comparing(p -> p.getName().getValue()))
            .map(details -> ProjectCardVM.apply(details, executor, out))
            .collect(Collectors.toList());

        return apply(cards.size(), cards);
    }

    @Override
    public CommandResult toCommandResult(ObjectMapper om) {
        DataTable dt = DataTables.createProjectsVM(projects);
        return CommandResult.success(dt.toAscii(), dt);
    }
}
