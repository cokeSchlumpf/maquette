package maquette.controller.domain.api.datasets;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

import org.apache.avro.Schema;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.Dataset;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeDatasetDescription;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeDatasetPrivacy;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.DeleteDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.GrantDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PushData;
import maquette.controller.domain.entities.dataset.protocol.commands.RevokeDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedDatasetDescription;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedDatasetPrivacy;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.DeletedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.GrantedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.PublishedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.PushedData;
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.queries.GetData;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.protocol.queries.GetVersionDetails;
import maquette.controller.domain.entities.dataset.protocol.results.GetDataResult;
import maquette.controller.domain.entities.dataset.protocol.results.GetDetailsResult;
import maquette.controller.domain.entities.dataset.protocol.results.GetVersionDetailsResult;
import maquette.controller.domain.entities.project.Project;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.commands.RegisterDataset;
import maquette.controller.domain.entities.project.protocol.commands.RemoveDataset;
import maquette.controller.domain.entities.project.protocol.events.RegisteredDataset;
import maquette.controller.domain.entities.project.protocol.events.RemovedDataset;
import maquette.controller.domain.entities.project.protocol.queries.GetProjectDetails;
import maquette.controller.domain.entities.project.protocol.results.GetProjectDetailsResult;
import maquette.controller.domain.entities.user.protocol.UserMessage;
import maquette.controller.domain.entities.user.protocol.commands.RegisterAccessToken;
import maquette.controller.domain.entities.user.protocol.events.RegisteredAccessToken;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.records.Records;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.Token;
import maquette.controller.domain.values.iam.TokenAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserId;
import maquette.controller.domain.values.project.ProjectDetails;

@AllArgsConstructor(staticName = "apply")
public final class DatasetsImpl implements Datasets {

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

    private final ActorRef<ShardingEnvelope<UserMessage>> users;

    private final ActorRef<ShardingEnvelope<ProjectMessage>> projects;

    private final ActorPatterns patterns;

    private final Materializer materializer;

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

    private CompletionStage<ProjectDetails> getProjectDetails(ResourceName project) {
        return patterns
            .ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(project),
                    GetProjectDetails.apply(project, replyTo, errorTo)),
                GetProjectDetailsResult.class)
            .thenApply(GetProjectDetailsResult::getDetails);
    }

    @Override
    public CompletionStage<DatasetDetails> changeDescription(User executor, ResourcePath dataset, Markdown description) {
        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(dataset),
                    ChangeDatasetDescription.apply(dataset, executor, description, replyTo, errorTo)),
                ChangedDatasetDescription.class)
            .thenCompose(result -> getDetails(dataset));
    }

    @Override
    public CompletionStage<DatasetDetails> changePrivacy(User executor, ResourcePath dataset, boolean isPrivate) {
        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(dataset),
                    ChangeDatasetPrivacy.apply(dataset, executor, isPrivate, replyTo, errorTo)),
                ChangedDatasetPrivacy.class)
            .thenCompose(result -> getDetails(dataset));
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
    public CompletionStage<DatasetDetails> createDataset(User executor, ResourcePath name, boolean isPrivate) {
        return patterns
            .ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(name.getProject()),
                    RegisterDataset.apply(name.getProject(), name.getName(), replyTo, errorTo)),
                RegisteredDataset.class)
            .thenCompose(result -> patterns.ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(name),
                    CreateDataset.apply(name, executor, isPrivate, replyTo, errorTo)),
                CreatedDataset.class))
            .thenCompose(result -> getProjectDetails(name.getProject())
                .thenCompose(proj -> patterns.ask(
                    datasets,
                    (replyTo, errorTo) -> ShardingEnvelope.apply(
                        Dataset.createEntityId(name),
                        ChangeOwner.apply(executor, name, proj.getAcl().getOwner().getAuthorization(), replyTo, errorTo)),
                    ChangedOwner.class)))
            .thenCompose(result -> getDetails(name));
    }

    @Override
    public CompletionStage<Token> createDatasetConsumerToken(User executor, UserId forUser, ResourcePath dataset) {
        ResourceName tokenName = ResourceName.apply(String.format("consumer-%s", UID.apply(8)));

        return patterns
            .ask(
                users,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    maquette.controller.domain.entities.user.User.createEntityId(forUser),
                    RegisterAccessToken.apply(executor, tokenName, replyTo, errorTo)),
                RegisteredAccessToken.class)
            .thenCompose(registered -> grantDatasetAccess(
                executor, dataset, DatasetPrivilege.CONSUMER,
                TokenAuthorization.apply(registered.getToken().getDetails().getId()))
                .thenApply(details -> registered.getToken()));
    }

    @Override
    public CompletionStage<Token> createDatasetProducerToken(User executor, UserId forUser, ResourcePath dataset) {
        ResourceName tokenName = ResourceName.apply(String.format("producer-%s", UID.apply(8)));

        return patterns
            .ask(
                users,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    maquette.controller.domain.entities.user.User.createEntityId(forUser),
                    RegisterAccessToken.apply(executor, tokenName, replyTo, errorTo)),
                RegisteredAccessToken.class)
            .thenCompose(registered -> grantDatasetAccess(
                executor, dataset, DatasetPrivilege.PRODUCER,
                TokenAuthorization.apply(registered.getToken().getDetails().getId()))
                .thenApply(details -> registered.getToken()));
    }

    @Override
    public CompletionStage<UID> createDatasetVersion(User executor, ResourcePath dataset, Schema schema) {
        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(dataset),
                    CreateDatasetVersion.apply(executor, dataset, schema, replyTo, errorTo)),
                CreatedDatasetVersion.class)
            .thenApply(CreatedDatasetVersion::getVersionId);
    }

    @Override
    public CompletionStage<Done> deleteDataset(User executor, ResourcePath dataset) {
        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(dataset),
                    DeleteDataset.apply(dataset, executor, replyTo, errorTo)),
                DeletedDataset.class)
            .thenCompose(deleted -> patterns.ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(dataset.getProject()),
                    RemoveDataset.apply(dataset.getProject(), dataset.getName(), replyTo, errorTo)),
                RemovedDataset.class))
            .thenApply(removed -> Done.getInstance());
    }

    @Override
    public CompletionStage<Records> getData(User executor, ResourcePath dataset) {
        return getDetails(dataset)
            .thenApply(DatasetDetails::findLatestVersion)
            .thenCompose(uid -> patterns.ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(dataset),
                    GetData.apply(executor, dataset, uid, replyTo, errorTo)),
                GetDataResult.class))
            .thenApply(GetDataResult::getRecords);
    }

    @Override
    public CompletionStage<Records> getData(User executor, ResourcePath dataset, VersionTag version) {
        return getDetails(dataset)
            .thenApply(details -> details.findVersionId(version))
            .thenCompose(uid -> patterns.ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(dataset),
                    GetData.apply(executor, dataset, uid, replyTo, errorTo)),
                GetDataResult.class))
            .thenApply(GetDataResult::getRecords);
    }

    @Override
    public CompletionStage<DatasetDetails> getDetails(User executor, ResourcePath dataset) {
        return getDetails(dataset);
    }

    @Override
    public CompletionStage<VersionDetails> getVersionDetails(User executor, ResourcePath dataset) {
        return getDetails(dataset)
            .thenApply(DatasetDetails::findLatestVersion)
            .thenCompose(uid -> patterns.ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(dataset),
                    GetVersionDetails.apply(dataset, uid, replyTo, errorTo)),
                GetVersionDetailsResult.class))
            .thenApply(GetVersionDetailsResult::getDetails);
    }

    @Override
    public CompletionStage<VersionDetails> getVersionDetails(User executor, ResourcePath dataset, VersionTag version) {
        return getDetails(dataset)
            .thenApply(details -> details.findVersionId(version))
            .thenCompose(uid -> patterns.ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(dataset),
                    GetVersionDetails.apply(dataset, uid, replyTo, errorTo)),
                GetVersionDetailsResult.class))
            .thenApply(GetVersionDetailsResult::getDetails);
    }

    @Override
    public CompletionStage<DatasetDetails> grantDatasetAccess(
        User executor, ResourcePath dataset, DatasetPrivilege grant, Authorization grantFor) {

        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(dataset),
                    GrantDatasetAccess.apply(dataset, executor, grant, grantFor, replyTo, errorTo)),
                GrantedDatasetAccess.class)
            .thenCompose(result -> getDetails(dataset));
    }

    @Override
    public CompletionStage<VersionDetails> pushData(User executor, ResourcePath dataset, UID versionId,
                                                    Source<ByteBuffer, NotUsed> data) {
        Path tmpFile = Operators.suppressExceptions(() -> Files.createTempFile("maquette", "upload"));

        return data
            .map(ByteString::fromByteBuffer)
            .runWith(FileIO.toPath(tmpFile), materializer)
            .thenCompose(written -> {
                try {
                    Records records = Records.fromFile(tmpFile);
                    return pushData(executor, dataset, versionId, records);
                } finally {
                    Operators.ignoreExceptions(() -> Files.delete(tmpFile));
                }
            });
    }

    private CompletionStage<VersionDetails> pushData(User executor, ResourcePath dataset, UID versionId, Records records) {
        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(dataset),
                    PushData.apply(executor, dataset, versionId, records, replyTo, errorTo)),
                PushedData.class)
            .thenApply(PushedData::getDetails);
    }

    @Override
    public CompletionStage<VersionTag> publishDatasetVersion(User executor, ResourcePath dataset, UID versionId,
                                                             String message) {
        return patterns
            .ask(
                datasets,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Dataset.createEntityId(dataset),
                    PublishDatasetVersion.apply(executor, dataset, versionId, message, replyTo, errorTo)),
                PublishedDatasetVersion.class)
            .thenApply(published -> published.getVersion().getVersion());
    }

    @Override
    public CompletionStage<VersionTag> putData(
        User executor, ResourcePath dataset, Source<ByteBuffer, NotUsed> data,
        String message) {

        Path tmpFile = Operators.suppressExceptions(() -> Files.createTempFile("maquette", "upload"));

        return data
            .map(ByteString::fromByteBuffer)
            .runWith(FileIO.toPath(tmpFile), materializer)
            .thenCompose(written -> {
                try {
                    Records records = Records.fromFile(tmpFile);
                    Schema schema = records.getSchema();

                    return createDatasetVersion(executor, dataset, schema)
                        .thenCompose(uid -> pushData(executor, dataset, uid, records))
                        .thenCompose(vd -> publishDatasetVersion(executor, dataset, vd.getVersionId(), message));
                } finally {
                    Operators.ignoreExceptions(() -> Files.delete(tmpFile));
                }
            });
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
