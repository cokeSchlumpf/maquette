package maquette.controller.domain.api.commands.views;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectDetails;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectCardVM {

    private static final String NAME = "name";
    private static final String CAN_CONSUME = "can-consume";
    private static final String CAN_PRODUCE = "can-produce";
    private static final String CAN_MANAGE = "can-manage";
    private static final String DESCRIPTION = "description";
    private static final String DATASETS = "datasets";
    private static final String LAST_UPDATE = "last-update";
    private static final String DATA = "data";

    @JsonProperty(NAME)
    private final String name;

    @JsonProperty(CAN_CONSUME)
    private final boolean canConsume;

    @JsonProperty(CAN_PRODUCE)
    private final boolean canProduce;

    @JsonProperty(CAN_MANAGE)
    private final boolean canManage;

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
        @JsonProperty(CAN_CONSUME) boolean canConsume,
        @JsonProperty(CAN_PRODUCE) boolean canProduce,
        @JsonProperty(CAN_MANAGE) boolean canManage,
        @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(DATASETS) int datasets,
        @JsonProperty(LAST_UPDATE) String lastUpdate,
        @JsonProperty(DATA) ProjectDetails data) {

        return new ProjectCardVM(name, canConsume, canProduce, canManage, description, datasets, lastUpdate, data);
    }

    public static ProjectCardVM apply(ProjectDetails details, User executor, OutputFormat out) {
        return apply(
            details.getName().getValue(),
            details.getAcl().canConsume(executor),
            details.getAcl().canProduce(executor),
            details.getAcl().canManage(executor),
            details.getDescription().asPreviewText(),
            details.getDatasets().size(),
            out.format(details.getModified()),
            details);
    }

}
