package maquette.controller.domain.values.notification.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShowDataset implements NotificationAction {

    private static final String DATASET = "dataset";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;
    
    @JsonCreator
    public static ShowDataset apply(@JsonProperty(DATASET) ResourcePath dataset) {
        return new ShowDataset(dataset);
    }

}
