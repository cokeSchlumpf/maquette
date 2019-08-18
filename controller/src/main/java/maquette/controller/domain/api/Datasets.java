package maquette.controller.domain.api;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import akka.Done;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetInfo;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.dataset.VersionInfo;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.User;

public final class Datasets {

    public CompletionStage<DatasetInfo> createDataset(User executor, ResourcePath name) {
        // Ask Namespace whether executor is allowed
        // If yes, create dataset
        return null;
    }

    public CompletionStage<VersionInfo> createDatasetVersion(User executor, ResourcePath datasetName) {
        // Ask Namespace whether executor is allowed
        // If not, ask Dataset whether executor is allowed
        // If allowed, create the version
        return null;
    }

    public CompletionStage<Done> deleteDataset(User executor, ResourcePath datasetName) {
        // Ask Namespace whether executor is allowed
        // If not, ask Dataset whether executor is allowed
        // If allowed, create the version
        return null;
    }

    public CompletionStage<Done> grantDatasetAccess(
        User executor, ResourcePath datasetName, Set<DatasetPrivilege> grant, Authorization grantFor) {

        return null;
    }

}
