package maquette.controller.domain.api.datasets;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.apache.avro.Schema;

import akka.NotUsed;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.stream.javadsl.Source;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.Dataset;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.protocol.results.GetDetailsResult;
import maquette.controller.domain.entities.namespace.Namespace;
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.queries.GetNamespaceDetails;
import maquette.controller.domain.entities.namespace.protocol.results.GetNamespaceDetailsResult;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.records.Records;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.exceptions.NotAuthorizedException;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.Token;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserId;
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

    private CompletionStage<Boolean> canGrant(ResourcePath datasetName, User executor) {
        return getDatasetDetails(datasetName)
            .thenCompose(details -> {
                if (details.getAcl().canGrantDatasetAccess(executor)) {
                    return CompletableFuture.completedFuture(true);
                } else {
                    return getNamespaceDetails(datasetName.getNamespace())
                        .thenApply(nsDetails -> nsDetails.getAcl().canGrantNamespaceAccess(executor));
                }
            });
    }

    private CompletionStage<Boolean> canManage(ResourcePath dataset, User executor) {
        return getDatasetDetails(dataset)
            .thenCompose(dsDetails -> {
                if (dsDetails.getAcl().canManage(executor)) {
                    return CompletableFuture.completedFuture(true);
                } else {
                    return getNamespaceDetails(dataset.getNamespace())
                        .thenApply(nsDetails -> nsDetails.getAcl().canManage(executor));
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

    private CompletionStage<Boolean> canRevoke(ResourcePath datasetName, User executor) {
        return getDatasetDetails(datasetName)
            .thenCompose(details -> {
                if (details.getAcl().canRevokeDatasetAccess(executor)) {
                    return CompletableFuture.completedFuture(true);
                } else {
                    return getNamespaceDetails(datasetName.getNamespace())
                        .thenApply(nsDetails -> nsDetails.getAcl().canRevokeNamespaceAccess(executor));
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
    public CompletionStage<DatasetDetails> changeDescription(User executor, ResourcePath dataset, Markdown description) {
        return canManage(dataset, executor)
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.changeDescription(executor, dataset, description);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<DatasetDetails> changePrivacy(User executor, ResourcePath dataset, boolean isPrivate) {
        return canManage(dataset, executor)
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.changePrivacy(executor, dataset, isPrivate);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<DatasetDetails> changeOwner(User executor, ResourcePath dataset, Authorization owner) {
        return getDatasetDetails(dataset)
            .thenCompose(dsDetails -> {
                if (dsDetails.getAcl().canManage(executor)) {
                    return CompletableFuture.completedFuture(true);
                } else {
                    return getNamespaceDetails(dataset.getNamespace())
                        .thenApply(nsDetails -> nsDetails.getAcl().canManage(executor));
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
    public CompletionStage<Token> createDatasetConsumerToken(User executor, UserId forUser, ResourcePath name) {
        return canConsume(name, executor)
            .thenCompose(canDo -> {
                if (canDo && executor.getUserId().equals(forUser)) {
                    return delegate.createDatasetConsumerToken(executor, forUser, name);
                } else {
                    return canGrant(name, executor).thenCompose(canGrant -> {
                        if (canGrant) {
                            return delegate.createDatasetConsumerToken(executor, forUser, name);
                        } else {
                            throw NotAuthorizedException.apply(executor);
                        }
                    });
                }
            });
    }

    @Override
    public CompletionStage<Token> createDatasetProducerToken(User executor, UserId forUser, ResourcePath name) {
        return canProduce(name, executor)
            .thenCompose(canDo -> {
                if (canDo && executor.getUserId().equals(forUser)) {
                    return delegate.createDatasetProducerToken(executor, forUser, name);
                } else {
                    return canGrant(name, executor).thenCompose(canGrant -> {
                        if (canGrant) {
                            return delegate.createDatasetProducerToken(executor, forUser, name);
                        } else {
                            throw NotAuthorizedException.apply(executor);
                        }
                    });
                }
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
    public CompletionStage<Records> getData(User executor, ResourcePath dataset) {
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
    public CompletionStage<Records> getData(User executor, ResourcePath dataset, VersionTag version) {
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
        return canGrant(datasetName, executor)
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
                                                    Source<ByteBuffer, NotUsed> data) {
        return canProduce(dataset, executor)
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.pushData(executor, dataset, versionId, data);
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
    public CompletionStage<VersionTag> putData(User executor, ResourcePath dataset, Source<ByteBuffer, NotUsed> data,
                                               String message) {
        return canProduce(dataset, executor)
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.putData(executor, dataset, data, message);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

    @Override
    public CompletionStage<DatasetDetails> revokeDatasetAccess(User executor, ResourcePath datasetName, DatasetPrivilege revoke,
                                                               Authorization revokeFrom) {
        return canRevoke(datasetName, executor)
            .thenCompose(canDo -> {
                if (canDo) {
                    return delegate.revokeDatasetAccess(executor, datasetName, revoke, revokeFrom);
                } else {
                    throw NotAuthorizedException.apply(executor);
                }
            });
    }

}
