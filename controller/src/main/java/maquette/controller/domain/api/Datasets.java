package maquette.controller.domain.api;

import java.util.concurrent.CompletionStage;

import akka.Done;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.User;

public interface Datasets {

    CompletionStage<DatasetDetails> createDataset(User executor, ResourcePath name);

    CompletionStage<Done> deleteDataset(User executor, ResourcePath datasetName);

    CompletionStage<DatasetDetails> grantDatasetAccess(
        User executor, ResourcePath datasetName, DatasetPrivilege grant, Authorization grantFor);

    CompletionStage<DatasetDetails> revokeDatasetAccess(
        User executor, ResourcePath datasetName, DatasetPrivilege revoke, Authorization revokeFrom);

}
