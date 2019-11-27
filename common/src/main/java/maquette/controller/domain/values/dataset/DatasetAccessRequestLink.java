package maquette.controller.domain.values.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasetAccessRequestLink {

    private static final String DATASET = "dataset";
    private static final String ID = "id";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(ID)
    private final UID id;

    @JsonCreator
    public static DatasetAccessRequestLink apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(ID) UID id) {

        return new DatasetAccessRequestLink(dataset, id);
    }

}
