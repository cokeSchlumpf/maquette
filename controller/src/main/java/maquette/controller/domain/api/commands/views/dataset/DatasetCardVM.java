package maquette.controller.domain.api.commands.views.dataset;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatasetCardVM {

    private static final String NAME = "name";
    private static final String CAN_CONSUME = "can-consume";
    private static final String CAN_PRODUCE = "can-produce";
    private static final String CAN_MANAGE = "can-manage";
    private static final String DESCRIPTION = "description";
    private static final String VERSIONS = "versions";
    private static final String LAST_UPDATE = "last-update";
    private static final String DATA = "data";

    @JsonProperty(NAME)
    private final String name;

    @JsonProperty(DESCRIPTION)
    private final String description;

    @JsonProperty(CAN_CONSUME)
    private final boolean canConsume;

    @JsonProperty(CAN_PRODUCE)
    private final boolean canProduce;

    @JsonProperty(CAN_MANAGE)
    private final boolean canManage;

    @JsonProperty(VERSIONS)
    private final int versions;

    @JsonProperty(LAST_UPDATE)
    private final String lastUpdate;

    @JsonProperty(DATA)
    private final DatasetDetails data;

    public static DatasetCardVM apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(CAN_CONSUME) boolean canConsume,
        @JsonProperty(CAN_PRODUCE) boolean canProduce,
        @JsonProperty(CAN_MANAGE) boolean canManage,
        @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(VERSIONS) int datasets,
        @JsonProperty(LAST_UPDATE) String lastUpdate,
        @JsonProperty(DATA) DatasetDetails data) {

        return new DatasetCardVM(name, description, canConsume, canProduce, canManage, datasets, lastUpdate, data);
    }

    public static DatasetCardVM apply(DatasetDetails details, User executor, OutputFormat out) {
        return apply(
            details.getDataset().toString(),
            details.getAcl().canConsume(executor),
            details.getAcl().canProduce(executor),
            details.getAcl().canManage(executor),
            details.getDescription().orElse(Markdown.apply()).asPreviewText(),
            details.getVersions().size(),
            out.format(details.getModified()),
            details);
    }

}
