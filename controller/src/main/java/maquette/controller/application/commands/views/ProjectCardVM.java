package maquette.controller.application.commands.views;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.application.commands.OutputFormat;
import maquette.controller.domain.values.project.ProjectDetails;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectCardVM {

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String DATASETS = "datasets";
    private static final String LAST_UPDATE = "last-update";
    private static final String DATA = "data";

    @JsonProperty(NAME)
    private final String name;

    @JsonProperty(DESCRIPTION)
    private final String description;

    @JsonProperty(DATASETS)
    private final int datasets;

    @JsonProperty(LAST_UPDATE)
    private final String lastUpdate;

    @JsonProperty(DATA)
    private final ProjectDetails data;

    public static ProjectCardVM apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(DATASETS) int datasets,
        @JsonProperty(LAST_UPDATE) String lastUpdate,
        @JsonProperty(DATA) ProjectDetails data) {

        return new ProjectCardVM(name, description, datasets, lastUpdate, data);
    }

    public static ProjectCardVM apply(ProjectDetails details, OutputFormat out) {
        return apply(
            details.getName().getValue(),
            details.getDescription().getHTMLString(),
            details.getDatasets().size(),
            out.format(details.getModified()),
            details);
    }

}
