package maquette.controller.domain.api.datasets;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.Dataset;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.protocol.results.GetDetailsResult;
import maquette.controller.domain.entities.namespace.Namespace;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceDetails;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceDetailsResult;
import maquette.controller.domain.exceptions.NotAuthorizedException;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespaceDetails;

@AllArgsConstructor(staticName = "apply")
public final class DatasetsSecured implements Datasets {

    private final ActorRef<ShardingEnvelope<NamespaceMessage>> namespaces;

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

    private final ActorPatterns patterns;

    private final Datasets delegate;

    private CompletionStage<Boolean> canConsume(ResourcePath dataset, User executor) {
        return getDatasetDetails(dataset)
            .thenCompose(dsDetails -> {
                if (dsDetails.getAcl().canConsume(executor)) {
                    return CompletableFuture.completedFuture(true);
                } else {
                    return getNamespaceDetails(dataset.getNamespace())
                        .thenApply(nsDetails -> nsDetails.getAcl().canConsume(executor));
                }
            });
    }

    private CompletionStage<Boolean> canProduce(ResourcePath dataset, User executor) {
        return getDatasetDetails(dataset)
            .thenCompose(dsDetails -> {
                if (dsDetails.getAcl().canProduce(executor)) {
                    return CompletableFuture.completedFuture(true);
                } else {
                    return getNamespaceDetails(dataset.getNamespace())
                        .thenApply(nsDetails -> nsDetails.getAcl().canProduce(executor));
                }
            });
    }

    private CompletionStage<DatasetDetails> getDatasetDetails(ResourcePath dataset) {
        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(dataset),
                    GetDetails.apply(dataset, replyTo, errorTo)),
                GetDetailsResult.class)
            .thenApply(GetDetailsResult::getDetails);
    }

    private CompletionStage<NamespaceDetails> getNamespaceDetails(ResourceName namespace) {
        return patterns
            .ask(
                namespaces,
                (replyTo, errorTo) ->
                    ShardingEnvelope.apply(
                        Namespace.createEntityId(namespace),
                        GetNamespaceDetails.apply(namespace, replyTo, errorTo)),
                GetNamespaceDetailsResult.class)
            .thenApply(GetNamespaceDetailsResult::getNamespaceDetails);
    }

    @Override
    public CompletionStage<DatasetDetails> changeOwner(User executor, ResourcePath dataset, Authorization owner) {
        return getDatasetDetails(dataset)
            .thenCompose(dsDetails -> {
                if (dsDetails.getAcl().canChangeOwner(executor)) {
                    return CompletableFuture.completedFuture(true);
                } else {
                    return getNamespaceDetails(dataset.getNamespace())
                        .thenApply(nsDetails -> nsDetails.getAcl().canChangeOwner(executor));
                }
            })
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.changeOwner(executor, dataset, owner);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });

    }

    @Override
    public CompletionStage<DatasetDetails> createDataset(User executor, ResourcePath name) {
        return getNamespaceDetails(name.getNamespace())
            .thenCompose(nsDetails -> {
                if (nsDetails.getAcl().canCreatedDataset(executor)) {
                    return delegate.createDataset(executor, name);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            })
            .thenCompose(dsDetails -> getNamespaceDetails(name.getNamespace()))
            .thenCompose(nsDetails -> {
                Authorization owner = nsDetails.getAcl().getOwner().getAuthorization();
                return delegate.changeOwner(executor, name, owner);
            });
    }

    @Override
    public CompletionStage<UID> createDatasetVersion(User executor, ResourcePath dataset, Schema schema) {
        return canProduce(dataset, executor)
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.createDatasetVersion(executor, dataset, schema);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<Done> deleteDataset(User executor, ResourcePath datasetName) {
        return getNamespaceDetails(datasetName.getNamespace())
            .thenCompose(details -> {
                if (details.getAcl().canDeleteNamespace(executor)) {
                    return delegate.deleteDataset(executor, datasetName);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<List<GenericData.Record>> getData(User executor, ResourcePath dataset) {
        return canConsume(dataset, executor)
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.getData(executor, dataset);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<List<GenericData.Record>> getData(User executor, ResourcePath dataset, VersionTag version) {
        return canConsume(dataset, executor)
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.getData(executor, dataset, version);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<DatasetDetails> getDetails(User executor, ResourcePath dataset) {
        return getDatasetDetails(dataset)
            .thenCompose(dsDetails -> {
                if (dsDetails.getAcl().canReadDetails(executor)) {
                    return CompletableFuture.completedFuture(true);
                } else {
                    return getNamespaceDetails(dataset.getNamespace())
                        .thenApply(nsDetails -> nsDetails.getAcl().canReadDetails(executor));
                }
            })
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.getDetails(executor, dataset);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<VersionDetails> getVersionDetails(User executor, ResourcePath dataset) {
        return getDatasetDetails(dataset)
            .thenCompose(dsDetails -> {
                if (dsDetails.getAcl().canReadDetails(executor)) {
                    return CompletableFuture.completedFuture(true);
                } else {
                    return getNamespaceDetails(dataset.getNamespace())
                        .thenApply(nsDetails -> nsDetails.getAcl().canReadDetails(executor));
                }
            })
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.getVersionDetails(executor, dataset);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<VersionDetails> getVersionDetails(User executor, ResourcePath dataset, VersionTag version) {
        return getDatasetDetails(dataset)
            .thenCompose(dsDetails -> {
                if (dsDetails.getAcl().canReadDetails(executor)) {
                    return CompletableFuture.completedFuture(true);
                } else {
                    return getNamespaceDetails(dataset.getNamespace())
                        .thenApply(nsDetails -> nsDetails.getAcl().canReadDetails(executor));
                }
            })
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.getVersionDetails(executor, dataset, version);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<DatasetDetails> grantDatasetAccess(User executor, ResourcePath datasetName, DatasetPrivilege grant,
                                                              Authorization grantFor) {
        return getDatasetDetails(datasetName)
            .thenCompose(details -> {
                if (details.getAcl().canGrantDatasetAccess(executor)) {
                    return CompletableFuture.completedFuture(true);
                } else {
                    return getNamespaceDetails(datasetName.getNamespace())
                        .thenApply(nsDetails -> nsDetails.getAcl().canGrantNamespaceAccess(executor));
                }
            })
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.grantDatasetAccess(executor, datasetName, grant, grantFor);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<VersionDetails> pushData(User executor, ResourcePath dataset, UID versionId,
                                                    List<GenericData.Record> records) {
        return canProduce(dataset, executor)
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.pushData(executor, dataset, versionId, records);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<VersionTag> publishDatasetVersion(User executor, ResourcePath dataset, UID versionId, String message) {
        return canProduce(dataset, executor)
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.publishDatasetVersion(executor, dataset, versionId, message);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<DatasetDetails> revokeDatasetAccess(User executor, ResourcePath datasetName, DatasetPrivilege revoke,
                                                               Authorization revokeFrom) {
        return getDatasetDetails(datasetName)
            .thenCompose(details -> {
                if (details.getAcl().canRevokeDatasetAccess(executor)) {
                    return CompletableFuture.completedFuture(true);
                } else {
                    return getNamespaceDetails(datasetName.getNamespace())
                        .thenApply(nsDetails -> nsDetails.getAcl().canRevokeNamespaceAccess(executor));
                }
            })
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.revokeDatasetAccess(executor, datasetName, revoke, revokeFrom);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

}
