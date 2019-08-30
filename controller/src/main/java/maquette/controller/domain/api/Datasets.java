package maquette.controller.domain.api;

import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.xml.validation.Schema;

import org.apache.avro.generic.GenericData;

import akka.Done;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.User;

public interface Datasets {

    CompletionStage<DatasetDetails> changeOwner(User executor, ResourcePath dataset, Authorization owner);

    CompletionStage<DatasetDetails> createDataset(User executor, ResourcePath name);

    CompletionStage<VersionDetails> createDatasetVersion(User executor, ResourcePath dataset, Schema schema);

    CompletionStage<Done> deleteDataset(User executor, ResourcePath datasetName);

    CompletionStage<DatasetDetails> getDetails(User executor, ResourcePath dataset);

    CompletionStage<DatasetDetails> grantDatasetAccess(
        User executor, ResourcePath datasetName, DatasetPrivilege grant, Authorization grantFor);

    CompletionStage<VersionDetails> pushData(
        User executor, ResourcePath dataset, UID versionId, List<GenericData.Record> records);

    CompletionStage<VersionDetails> publishDatasetVersion(
        User executor, ResourcePath dataset, UID versionId, String message);

    CompletionStage<DatasetDetails> revokeDatasetAccess(
        User executor, ResourcePath datasetName, DatasetPrivilege revoke, Authorization revokeFrom);

}
