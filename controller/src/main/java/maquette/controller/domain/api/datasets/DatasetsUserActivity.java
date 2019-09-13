package maquette.controller.domain.api.datasets;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import org.apache.avro.Schema;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.services.CreateDefaultNamespace;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.records.Records;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(staticName = "apply")
public final class DatasetsUserActivity implements Datasets {

    private final Datasets delegate;

    private final CreateDefaultNamespace createDefaultNamespace;

    public static DatasetsUserActivity apply(
        ActorRef<NamespacesMessage> namespacesMgr, ActorRef<ShardingEnvelope<NamespaceMessage>> namespaces,
        ActorPatterns patterns, Datasets delegate) {

        CreateDefaultNamespace cdn = CreateDefaultNamespace.apply(namespacesMgr, namespaces, patterns);
        return apply(delegate, cdn);
    }

    private <T> CompletionStage<T> createDefaultNamespace(User executor, Function<Datasets, CompletionStage<T>> andThen) {
        return createDefaultNamespace.run(executor, () -> andThen.apply(delegate));
    }

    @Override
    public CompletionStage<DatasetDetails> changeOwner(User executor, ResourcePath dataset, Authorization owner) {
        return createDefaultNamespace(executor, d -> d.changeOwner(executor, dataset, owner));
    }

    @Override
    public CompletionStage<DatasetDetails> createDataset(User executor, ResourcePath name) {
        return createDefaultNamespace(executor, d -> d.createDataset(executor, name));
    }

    @Override
    public CompletionStage<UID> createDatasetVersion(User executor, ResourcePath dataset, Schema schema) {
        return createDefaultNamespace(executor, d -> d.createDatasetVersion(executor, dataset, schema));
    }

    @Override
    public CompletionStage<Done> deleteDataset(User executor, ResourcePath datasetName) {
        return createDefaultNamespace(executor, d -> d.deleteDataset(executor, datasetName));
    }

    @Override
    public CompletionStage<Records> getData(User executor, ResourcePath dataset) {
        return createDefaultNamespace(executor, d -> d.getData(executor, dataset));
    }

    @Override
    public CompletionStage<Records> getData(User executor, ResourcePath dataset, VersionTag version) {
        return createDefaultNamespace(executor, d -> d.getData(executor, dataset, version));
    }

    @Override
    public CompletionStage<DatasetDetails> getDetails(User executor, ResourcePath dataset) {
        return createDefaultNamespace(executor, d -> d.getDetails(executor, dataset));
    }

    @Override
    public CompletionStage<VersionDetails> getVersionDetails(User executor, ResourcePath dataset) {
        return createDefaultNamespace(executor, d -> d.getVersionDetails(executor, dataset));
    }

    @Override
    public CompletionStage<VersionDetails> getVersionDetails(User executor, ResourcePath dataset, VersionTag version) {
        return createDefaultNamespace(executor, d -> d.getVersionDetails(executor, dataset, version));
    }

    @Override
    public CompletionStage<DatasetDetails> grantDatasetAccess(User executor, ResourcePath datasetName, DatasetPrivilege grant,
                                                              Authorization grantFor) {
        return createDefaultNamespace(executor, d -> d.grantDatasetAccess(executor, datasetName, grant, grantFor));
    }

    @Override
    public CompletionStage<VersionDetails> pushData(User executor, ResourcePath dataset, UID versionId, Records records) {
        return createDefaultNamespace(executor, d -> d.pushData(executor, dataset, versionId, records));
    }

    @Override
    public CompletionStage<VersionTag> publishDatasetVersion(User executor, ResourcePath dataset, UID versionId, String message) {
        return createDefaultNamespace(executor, d -> d.publishDatasetVersion(executor, dataset, versionId, message));
    }

    @Override
    public CompletionStage<DatasetDetails> revokeDatasetAccess(User executor, ResourcePath datasetName, DatasetPrivilege revoke,
                                                               Authorization revokeFrom) {
        return createDefaultNamespace(executor, d -> d.revokeDatasetAccess(executor, datasetName, revoke, revokeFrom));
    }

}
