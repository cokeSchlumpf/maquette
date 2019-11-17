package maquette.controller.domain.api.datasets;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;

import org.apache.avro.Schema;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Source;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.governance.GovernanceProperties;
import maquette.controller.domain.values.core.records.Records;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetGrant;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.Token;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserId;

public interface Datasets {

    /**
     * Approve a dataset access request.
     *
     * @param executor
     *     The user which approves the request
     * @param id
     *     The id of the request
     * @param comment
     *     The comment for the approval
     * @return The updated request
     */
    CompletionStage<DatasetGrant> approveAccessRequest(User executor, ResourcePath dataset, UID id, Markdown comment);

    /**
     * Change the description of an existing dataset.
     *
     * @param executor
     *     The user which executes the command
     * @param dataset
     *     The dataset which description should be changed
     * @param description
     *     The new description
     * @return Updated details of the dataset
     */
    CompletionStage<DatasetDetails> changeDescription(User executor, ResourcePath dataset, Markdown description);

    /**
     * Change the description of an existing dataset.
     *
     * @param executor
     *     The user which executes the command
     * @param dataset
     *     The dataset which description should be changed
     * @param governance
     *     The new governance properties
     * @return Updated details of the dataset
     */
    CompletionStage<DatasetDetails> changeGovernance(User executor, ResourcePath dataset, GovernanceProperties governance);

    /**
     * Changes the privacy setting of a dataset.
     *
     * @param executor
     *     The user which executes the command
     * @param dataset
     *     The dataset which should be updated
     * @param isPrivate
     *     Whether the dataset should be private or not
     * @return Updated details of the dataset
     */
    CompletionStage<DatasetDetails> changePrivacy(User executor, ResourcePath dataset, boolean isPrivate);

    /**
     * Change the owner of an existing dataset.
     *
     * @param executor
     *     The user which executes the command
     * @param dataset
     *     The dataset which should be updated
     * @param owner
     *     The new owner of the dataset
     * @return Updated details of the dataset
     */
    CompletionStage<DatasetDetails> changeOwner(User executor, ResourcePath dataset, Authorization owner);

    /**
     * Create a new dataset.
     *
     * @param executor
     *     The user which executes the command
     * @param name
     *     The name of the new dataset
     * @param isPrivate
     *     Whether the dataset should be private or not
     * @return Updated details of the dataset
     */
    CompletionStage<DatasetDetails> createDataset(
        User executor, ResourcePath name, Markdown description, boolean isPrivate, GovernanceProperties governance);

    /**
     * Create a token for a user to consume from a dataset.
     *
     * @param executor
     *     The user which executes the command
     * @param forUser
     *     The user for which the consumer token should be created
     * @param dataset
     *     The dataset for which the token should be created
     * @return The created token
     */
    CompletionStage<Token> createDatasetConsumerToken(User executor, UserId forUser, ResourcePath dataset);

    /**
     * Create a token for a user to produce data to the dataset.
     *
     * @param executor
     *     The user which executes the command
     * @param forUser
     *     The user for which the producer token should be created
     * @param dataset
     *     The dataset for which the token should be created
     * @return The created token
     */
    CompletionStage<Token> createDatasetProducerToken(User executor, UserId forUser, ResourcePath dataset);

    /**
     * Create a new version of an existing dataset.
     *
     * @param executor
     *     The user which executes the command
     * @param dataset
     *     The dataset which should be updated
     * @param schema
     *     The data schema of the new version
     * @return The UID for the new version
     */
    CompletionStage<UID> createDatasetVersion(User executor, ResourcePath dataset, Schema schema);

    /**
     * Delete an existing dataset.
     *
     * @param executor
     *     The user which executes the command
     * @param dataset
     *     The dataset which should be deleted
     * @return Just done
     */
    CompletionStage<Done> deleteDataset(User executor, ResourcePath dataset);

    /**
     * Get the latest data from an existing dataset.
     *
     * @param executor
     *     The user which executes the query
     * @param dataset
     *     The dataset which should be queried
     * @return The records of the latest dataset version
     */
    CompletionStage<Records> getData(User executor, ResourcePath dataset);

    /**
     * Get the data from a specific dataset version.
     *
     * @param executor
     *     The user which executes the query
     * @param dataset
     *     The dataset which should be queried
     * @param version
     *     The version which should be fetched
     * @return The records of the dataset version
     */
    CompletionStage<Records> getData(User executor, ResourcePath dataset, VersionTag version);

    /**
     * Get details of the dataset.
     *
     * @param executor
     *     The user which executes the query
     * @param dataset
     *     The dataset which should be queried
     * @return The details of the dataset
     */
    CompletionStage<DatasetDetails> getDetails(User executor, ResourcePath dataset);

    /**
     * Get details of the latest dataset version.
     *
     * @param executor
     *     The user which executes the query
     * @param dataset
     *     The dataset which should be queried
     * @return The details of the latest version
     */
    CompletionStage<VersionDetails> getVersionDetails(User executor, ResourcePath dataset);

    /**
     * Get details of a specific dataset version
     *
     * @param executor
     *     The user which executes the query
     * @param dataset
     *     The dataset which should be queried
     * @param version
     *     The version which should be queried
     * @return The details of the specified version
     */
    CompletionStage<VersionDetails> getVersionDetails(User executor, ResourcePath dataset, VersionTag version);

    /**
     * Grant access to a dataset.
     *
     * @param executor
     *     The user which executes the query
     * @param dataset
     *     The dataset which should be updated
     * @param grant
     *     The privilege which should be granted
     * @param grantFor
     *     The user which should receive the privilege
     * @return The details of the updated dataset
     */
    CompletionStage<DatasetDetails> grantDatasetAccess(
        User executor, ResourcePath dataset, DatasetPrivilege grant, Authorization grantFor);

    /**
     * Append data to an unpublished dataset version.
     *
     * @param executor
     *     The user which executes the query
     * @param dataset
     *     The dataset which should be updated
     * @param versionId
     *     The version id of the unpublished version
     * @param data
     *     The data to be appended
     * @return Updated version details of the updated version
     */
    CompletionStage<VersionDetails> pushData(
        User executor, ResourcePath dataset, UID versionId, Source<ByteBuffer, NotUsed> data);

    /**
     * Publish a dataset version.
     *
     * @param executor
     *     The user which executes the query
     * @param dataset
     *     The dataset which should be updated
     * @param versionId
     *     The id of the unpublished dataset version
     * @param message
     *     A message containing information about the newly published version
     * @return The version tag for the published version
     */
    CompletionStage<VersionTag> publishDatasetVersion(
        User executor, ResourcePath dataset, UID versionId, String message);

    /**
     * Create a new dataset version which is immediately published with the provided data.
     *
     * @param executor
     *     The user which executes the query
     * @param dataset
     *     The dataset which should be updated
     * @param data
     *     The data for the new version
     * @param message
     *     A message containing information about the newly published version
     * @return The version tag for the published version
     */
    CompletionStage<VersionTag> putData(
        User executor, ResourcePath dataset, Source<ByteBuffer, NotUsed> data,
        String message);

    /**
     * An executor requests access to an existing dataset.
     *
     * @param executor
     *     The user which executes the command
     * @param dataset
     *     The dataset for which access should be requested
     * @param justification
     *     The reason why access is required
     * @param grant
     *     The access failure which is requested
     * @param grantFor
     *     The authorization for which access is requested
     * @return The initiated request
     */
    CompletionStage<DatasetGrant> requestDatasetAccess(
        User executor, ResourcePath dataset, Markdown justification, DatasetPrivilege grant, Authorization grantFor);

    /**
     * Revoke the access for an authorization from an existing dataset.
     *
     * @param executor
     *     The user which executes the query
     * @param datasetName
     *     The dataset which should be updated
     * @param revoke
     *     The privilege which should be revoked
     * @param revokeFrom
     *     Thee authorization which should loose the privilege
     * @return The updated details of the dataset
     */
    CompletionStage<DatasetDetails> revokeDatasetAccess(
        User executor, ResourcePath datasetName, DatasetPrivilege revoke, Authorization revokeFrom);

}
