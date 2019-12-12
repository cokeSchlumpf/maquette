package maquette.controller.domain.api.views.dataset;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.CommandResult;
import maquette.controller.domain.api.DataTable;
import maquette.controller.domain.api.OutputFormat;
import maquette.controller.domain.api.ViewModel;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.VersionTagInfo;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatasetVersionsVM implements ViewModel {

    private static final String COUNT = "count";
    private static final String VERSIONS = "versions";

    @JsonProperty(COUNT)
    private final int count;

    @JsonProperty(VERSIONS)
    private final List<DatasetVersionVM> versions;

    @JsonCreator
    public static DatasetVersionsVM apply(
        @JsonProperty(COUNT) int count,
        @JsonProperty(VERSIONS) List<DatasetVersionVM> versions) {

        return new DatasetVersionsVM(count, ImmutableList.copyOf(versions));
    }

    public static DatasetVersionsVM apply(List<VersionTagInfo> versions, OutputFormat of) {
        List<DatasetVersionVM> cards = versions
            .stream()
            .map(info -> DatasetVersionVM.apply(info, of))
            .collect(Collectors.toList());

        return apply(cards.size(), cards);
    }

    public static DatasetVersionsVM apply(DatasetDetails details, OutputFormat of) {
        Comparator<VersionTagInfo> comparing = Comparator
            .comparing(VersionTagInfo::getVersion)
            .reversed();

        List<VersionTagInfo> sorted = details
            .getVersions()
            .stream()
            .sorted(comparing)
            .collect(Collectors.toList());

        return apply(sorted, of);
    }

    @Override
    public CommandResult toCommandResult(ObjectMapper om) {
        DataTable dt = DataTable.apply("version", "records", "committed by", "committed at", "message");

        for (DatasetVersionVM v : this.getVersions()) {
            dt = dt.withRow(
                v.getVersion(),
                v.getRecords(),
                v.getCommittedBy(),
                v.getCommitted(),
                v.getMessage());
        }

        return CommandResult.success(dt.toAscii(), dt);
    }
}
