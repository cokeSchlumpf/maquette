package maquette.controller.domain.api.datasets;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import org.apache.avro.Schema;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Source;
import lombok.AllArgsConstructor;
import maquette.controller.domain.services.CreateDefaultNamespace;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.records.Records;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.Token;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserId;

@AllArgsConstructor(staticName = "apply")
public final class DatasetsUserActivity implements Datasets {

    private final Datasets delegate;

    private final CreateDefaultNamespace createDefaultNamespace;

    private <T> CompletionStage<T> createDefaultProject(User executor, Function<Datasets, CompletionStage<T>> andThen) {
        return createDefaultNamespace.run(executor, () -> andThen.apply(delegate));
    }

    @Override
    public CompletionStage<DatasetDetails> changeDescription(User executor, ResourcePath dataset, Markdown description) {
        return createDefaultProject(executor, d -> d.changeDescription(executor, dataset, description));
    }

    @Override
    public CompletionStage<DatasetDetails> changePrivacy(User executor, ResourcePath dataset, boolean isPrivate) {
        return createDefaultProject(executor, d -> d.changePrivacy(executor, dataset, isPrivate));
    }

    @Override
    public CompletionStage<DatasetDetails> changeOwner(User executor, ResourcePath dataset, Authorization owner) {
        return createDefaultProject(executor, d -> d.changeOwner(executor, dataset, owner));
    }

    @Override
    public CompletionStage<DatasetDetails> createDataset(User executor, ResourcePath name, boolean isPrivate) {
        return createDefaultProject(executor, d -> d.createDataset(executor, name, isPrivate));
    }

    @Override
    public CompletionStage<Token> createDatasetConsumerToken(User executor, UserId forUser, ResourcePath dataset) {
        return createDefaultProject(executor, d -> d.createDatasetConsumerToken(executor, forUser, dataset));
    }

    @Override
    public CompletionStage<Token> createDatasetProducerToken(User executor, UserId forUser, ResourcePath dataset) {
        return createDefaultProject(executor, d -> d.createDatasetProducerToken(executor, forUser, dataset));
    }

    @Override
    public CompletionStage<UID> createDatasetVersion(User executor, ResourcePath dataset, Schema schema) {
        return createDefaultProject(executor, d -> d.createDatasetVersion(executor, dataset, schema));
    }

    @Override
    public CompletionStage<Done> deleteDataset(User executor, ResourcePath dataset) {
        return createDefaultProject(executor, d -> d.deleteDataset(executor, dataset));
    }

    @Override
    public CompletionStage<Records> getData(User executor, ResourcePath dataset) {
        return createDefaultProject(executor, d -> d.getData(executor, dataset));
    }

    @Override
    public CompletionStage<Records> getData(User executor, ResourcePath dataset, VersionTag version) {
        return createDefaultProject(executor, d -> d.getData(executor, dataset, version));
    }

    @Override
    public CompletionStage<DatasetDetails> getDetails(User executor, ResourcePath dataset) {
        return createDefaultProject(executor, d -> d.getDetails(executor, dataset));
    }

    @Override
    public CompletionStage<VersionDetails> getVersionDetails(User executor, ResourcePath dataset) {
        return createDefaultProject(executor, d -> d.getVersionDetails(executor, dataset));
    }

    @Override
    public CompletionStage<VersionDetails> getVersionDetails(User executor, ResourcePath dataset, VersionTag version) {
        return createDefaultProject(executor, d -> d.getVersionDetails(executor, dataset, version));
    }

    @Override
    public CompletionStage<DatasetDetails> grantDatasetAccess(User executor, ResourcePath dataset, DatasetPrivilege grant,
                                                              Authorization grantFor) {
        return createDefaultProject(executor, d -> d.grantDatasetAccess(executor, dataset, grant, grantFor));
    }

    @Override
    public CompletionStage<VersionDetails> pushData(User executor, ResourcePath dataset, UID versionId,
                                                    Source<ByteBuffer, NotUsed> data) {
        return createDefaultProject(executor, d -> d.pushData(executor, dataset, versionId, data));
    }

    @Override
    public CompletionStage<VersionTag> publishDatasetVersion(User executor, ResourcePath dataset, UID versionId, String message) {
        return createDefaultProject(executor, d -> d.publishDatasetVersion(executor, dataset, versionId, message));
    }

    @Override
    public CompletionStage<VersionTag> putData(User executor, ResourcePath dataset, Source<ByteBuffer, NotUsed> data,
                                               String message) {
        return createDefaultProject(executor, d -> d.putData(executor, dataset, data, message));
    }

    @Override
    public CompletionStage<DatasetDetails> revokeDatasetAccess(User executor, ResourcePath datasetName, DatasetPrivilege revoke,
                                                               Authorization revokeFrom) {
        return createDefaultProject(executor, d -> d.revokeDatasetAccess(executor, datasetName, revoke, revokeFrom));
    }

}
