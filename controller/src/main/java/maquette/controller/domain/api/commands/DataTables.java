package maquette.controller.domain.api.commands;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.project.ProjectDetails;

public final class DataTables {

    public static DataTable createDatasets(Iterable<DatasetDetails> datasets) {
        DataTable dt = DataTable.apply("name", "versions", "owner", "is private", "modified", "modified by");

        for (DatasetDetails details : datasets) {
            dt = dt.withRow(
                details.getDataset(),
                details.getVersions().size(),
                details.getAcl().getOwner().getAuthorization(),
                details.getAcl().isPrivate(),
                details.getModified(),
                details.getModifiedBy());
        }

        return dt;
    }

    public static DataTable createProjects(Iterable<ProjectDetails> projects) {
        DataTable dt = DataTable.apply("name", "owner", "private", "modified", "datasets");

        List<ProjectDetails> sorted = Lists.newArrayList(projects)
            .stream()
            .sorted(Comparator.comparing(p -> p.getName().getValue()))
            .collect(Collectors.toList());

        for (ProjectDetails info : sorted) {
            dt = dt.withRow(
                info.getName(),
                info.getAcl().getOwner().getAuthorization(),
                info.getAcl().isPrivate(),
                info.getModified(),
                info.getDatasets().size());
        }

        return dt;
    }

}
