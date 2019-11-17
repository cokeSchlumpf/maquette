package maquette.controller.domain.values.iam;

import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.dataset.DatasetAccessRequestLink;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonalUserProfile {

    private static final String ID = "id";
    private static final String DATASET_ACCESS_REQUESTS = "dataset-access-requests";
    private static final String NAMESPACE = "namespace";

    @JsonProperty(ID)
    private final UserId id;

    @JsonProperty(DATASET_ACCESS_REQUESTS)
    private final Set<DatasetAccessRequestLink> datasetAccessRequests;

    @JsonProperty(NAMESPACE)
    private final ResourceName namespace;

    @JsonCreator
    public static PersonalUserProfile apply(
        @JsonProperty(ID) UserId id,
        @JsonProperty(DATASET_ACCESS_REQUESTS) Set<DatasetAccessRequestLink> datasetAccessRequestLinks,
        @JsonProperty(NAMESPACE) ResourceName namespace) {

        return new PersonalUserProfile(id, ImmutableSet.copyOf(datasetAccessRequestLinks), namespace);
    }

    public Optional<ResourceName> getNamespace() {
        return Optional.ofNullable(namespace);
    }

}
