package maquette.controller.adapters.cli;

import maquette.controller.domain.values.dataset.DatasetDetails;

public final class DataTables {

    public static DataTable createDatasets(Iterable<DatasetDetails> datasets) {
        DataTable dt = DataTable.apply("name", "versions", "owner", "is private", "modified", "modified by");

        for (DatasetDetails details : datasets) {
            dt = dt.withRow(
                details.getDataset(),
                details.getVersions().size(),
                details.getAcl().getOwner().getAuthorization(),
                details.getAcl().isPrivate(),
                details.getModified(),
                details.getModifiedBy());
        }

        return dt;
    }

}
