package maquette.controller.domain.api.commands.views;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.ViewModel;
import maquette.controller.domain.values.dataset.VersionTagInfo;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatasetVersionsVM implements ViewModel {

    private static final String COUNT = "count";
    private static final String VERSIONS = "versions";

    @JsonProperty(COUNT)
    private final int count;

    @JsonProperty(VERSIONS)
    private final List<DatasetVersionCardVM> versions;

    @JsonCreator
    public static DatasetVersionsVM apply(
        @JsonProperty(COUNT) int count,
        @JsonProperty(VERSIONS) List<DatasetVersionCardVM> versions) {

        return new DatasetVersionsVM(count, ImmutableList.copyOf(versions));
    }

    public static DatasetVersionsVM apply(List<VersionTagInfo> versions, OutputFormat of) {
        List<DatasetVersionCardVM> cards = versions
            .stream()
            .map(info -> DatasetVersionCardVM.apply(info, of))
            .collect(Collectors.toList());

        return apply(cards.size(), cards);
    }

}
