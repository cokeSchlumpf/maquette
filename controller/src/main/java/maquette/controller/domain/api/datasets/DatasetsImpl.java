package maquette.controller.domain.api.datasets;

import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.xml.validation.Schema;

import org.apache.avro.generic.GenericData;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.Dataset;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.DeleteDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.GrantDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.commands.RevokeDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.DeletedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.GrantedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.protocol.results.GetDetailsResult;
import maquette.controller.domain.entities.namespace.Namespace;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.commands.RegisterDataset;
import maquette.controller.domain.entities.namespace.protocol.commands.RemoveDataset;
import maquette.controller.domain.entities.namespace.protocol.events.RegisteredDataset;
import maquette.controller.domain.entities.namespace.protocol.events.RemovedDataset;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(staticName = "apply")
public final class DatasetsImpl implements Datasets {

    private final ActorRef<ShardingEnvelope<NamespaceMessage>> namespaces;

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

    private final ActorPatterns patterns;

    private CompletionStage<DatasetDetails> getDetails(ResourcePath dataset) {
        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(dataset),
                    GetDetails.apply(dataset, replyTo, errorTo)),
                GetDetailsResult.class)
            .thenApply(GetDetailsResult::getDetails);
    }

    @Override
    public CompletionStage<DatasetDetails> changeOwner(User executor, ResourcePath dataset, Authorization owner) {
        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(dataset),
                    ChangeOwner.apply(executor, dataset, owner, replyTo, errorTo)),
                ChangedOwner.class)
            .thenCompose(result -> getDetails(dataset));
    }

    @Override
    public CompletionStage<DatasetDetails> createDataset(User executor, ResourcePath name) {
        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(name.getNamespace()),
                    RegisterDataset.apply(name.getNamespace(), name.getName(), replyTo, errorTo)),
                RegisteredDataset.class)
            .thenCompose(result -> patterns.ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(name),
                    CreateDataset.apply(name, executor, replyTo, errorTo)),
                CreatedDataset.class))
            .thenCompose(result -> getDetails(name));
    }

    @Override
    public CompletionStage<VersionDetails> createDatasetVersion(User executor, ResourcePath dataset, Schema schema) {
        return null;
    }

    @Override
    public CompletionStage<Done> deleteDataset(User executor, ResourcePath datasetName) {
        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(datasetName),
                    DeleteDataset.apply(datasetName, executor, replyTo, errorTo)),
                DeletedDataset.class)
            .thenCompose(deleted -> patterns.ask(
                namespaces,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Namespace.createEntityId(datasetName.getNamespace()),
                    RemoveDataset.apply(datasetName.getNamespace(), datasetName.getName(), replyTo, errorTo)),
                RemovedDataset.class))
            .thenApply(removed -> Done.getInstance());
    }

    @Override
    public CompletionStage<List<GenericData.Record>> getData(User executor) {
        return null;
    }

    @Override
    public CompletionStage<List<GenericData.Record>> getData(User executor, VersionTag version) {
        return null;
    }

    @Override
    public CompletionStage<DatasetDetails> getDetails(User executor, ResourcePath dataset) {
        return getDetails(dataset);
    }

    @Override
    public CompletionStage<VersionDetails> getVersionDetails(User executor) {
        return null;
    }

    @Override
    public CompletionStage<VersionDetails> getVersionDetails(User executor, VersionTag version) {
        return null;
    }

    @Override
    public CompletionStage<DatasetDetails> grantDatasetAccess(
        User executor, ResourcePath datasetName, DatasetPrivilege grant, Authorization grantFor) {

        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(datasetName),
                    GrantDatasetAccess.apply(datasetName, executor, grant, grantFor, replyTo, errorTo)),
                GrantedDatasetAccess.class)
            .thenCompose(result -> getDetails(datasetName));
    }

    @Override
    public CompletionStage<VersionDetails> pushData(User executor, ResourcePath dataset, UID versionId,
                                                    List<GenericData.Record> records) {
        return null;
    }

    @Override
    public CompletionStage<VersionDetails> publishDatasetVersion(User executor, ResourcePath dataset, UID versionId,
                                                                 String message) {
        return null;
    }

    @Override
    public CompletionStage<DatasetDetails> revokeDatasetAccess(
        User executor, ResourcePath datasetName, DatasetPrivilege revoke, Authorization revokeFrom) {

        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(datasetName),
                    RevokeDatasetAccess.apply(datasetName, executor, revoke, revokeFrom, replyTo, errorTo)),
                RevokedDatasetAccess.class)
            .thenCompose(result -> getDetails(datasetName));
    }

}
