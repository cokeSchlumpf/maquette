package maquette.controller.domain.api.commands.views;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.ViewModel;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatasetsVM implements ViewModel {

    private static final String COUNT = "count";
    private static final String DATASETS = "datasets";

    @JsonProperty(COUNT)
    private final int count;

    @JsonProperty(DATASETS)
    private final List<DatasetCardVM> datasets;

    @JsonCreator
    public static DatasetsVM apply(
        @JsonProperty(COUNT) int count,
        @JsonProperty(DATASETS) List<DatasetCardVM> datasets) {

        return new DatasetsVM(count, ImmutableList.copyOf(datasets));
    }

    public static DatasetsVM apply(Collection<DatasetDetails> projects, User executor, OutputFormat out) {
        List<DatasetCardVM> cards = projects
            .stream()
            .sorted(Comparator.comparing(p -> p.getDataset().toString()))
            .map(details -> DatasetCardVM.apply(details, executor, out))
            .collect(Collectors.toList());

        return apply(cards.size(), cards);
    }

}
