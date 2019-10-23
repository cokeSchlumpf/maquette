package maquette.controller.domain.values.notification.actions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "action")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = ShowDataset.class, name = "show dataset"),
        @JsonSubTypes.Type(value = ShowDatasetAccessRequest.class, name = "show dataset-access-request")
    })
public interface NotificationAction {
}
