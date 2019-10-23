package maquette.controller.domain.entities.dataset.protocol;

import java.util.Map;

import com.google.common.collect.Maps;

import akka.actor.ExtendedActorSystem;
import maquette.controller.domain.entities.dataset.protocol.commands.ApproveDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeDatasetDescription;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeDatasetGovernance;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeDatasetPrivacy;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.dataset.protocol.commands.CommitDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.DeleteDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.GrantDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishCommittedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PushData;
import maquette.controller.domain.entities.dataset.protocol.commands.RevokeDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.ApprovedDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedDatasetDescription;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedDatasetGovernance;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedDatasetPrivacy;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.dataset.protocol.events.CommittedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetAccessRequest;
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
import maquette.controller.domain.util.databind.AbstractMessageSerializer;

public final class MessageSerializer extends AbstractMessageSerializer {

    protected MessageSerializer(ExtendedActorSystem actorSystem) {
        super(actorSystem, 2403 + 2);
    }

    @Override
    protected Map<String, Class<?>> getManifestToClass() {
        Map<String, Class<?>> m = Maps.newHashMap();

        m.put("dataset/commands/approve-dataset-access-request/v1", ApproveDatasetAccessRequest.class);
        m.put("dataset/commands/change-dataset-description/v1", ChangeDatasetDescription.class);
        m.put("dataset/commands/change-dataset-governance/v1", ChangeDatasetGovernance.class);
        m.put("dataset/commands/change-dataset-privacy/v1", ChangeDatasetPrivacy.class);
        m.put("dataset/commands/change-owner/v1", ChangeOwner.class);
        m.put("dataset/commands/commit-dataset-version/v1", CommitDatasetVersion.class);
        m.put("dataset/commands/create-dataset/v1", CreateDataset.class);
        m.put("dataset/commands/create-dataset-access-request/v1", CreateDatasetAccessRequest.class);
        m.put("dataset/commands/create-dataset-version/v1", CreateDatasetVersion.class);
        m.put("dataset/commands/delete-dataset/v1", DeleteDataset.class);
        m.put("dataset/commands/grant-dataset-access/v1", GrantDatasetAccess.class);
        m.put("dataset/commands/publish-committed-dataset-version/v1", PublishCommittedDatasetVersion.class);
        m.put("dataset/commands/publish-dataset-version/v1", PublishDatasetVersion.class);
        m.put("dataset/commands/push-data/v1", PushData.class);
        m.put("dataset/commands/revoke-dataset-access/v1", RevokeDatasetAccess.class);

        m.put("dataset/events/approved-dataset-access-request/v1", ApprovedDatasetAccessRequest.class);
        m.put("dataset/events/changed-dataset-description/v1", ChangedDatasetDescription.class);
        m.put("dataset/events/changed-dataset-governance/v1", ChangedDatasetGovernance.class);
        m.put("dataset/events/changed-dataset-privacy/v1", ChangedDatasetPrivacy.class);
        m.put("dataset/events/changed-owner/v1", ChangedOwner.class);
        m.put("dataset/events/committed-dataset-version/v1", CommittedDatasetVersion.class);
        m.put("dataset/events/created-dataset/v1", CreatedDataset.class);
        m.put("dataset/events/created-dataset-access-request/v1", CreatedDatasetAccessRequest.class);
        m.put("dataset/events/created-dataset-version/v1", CreatedDatasetVersion.class);
        m.put("dataset/events/deleted-dataset/v1", DeletedDataset.class);
        m.put("dataset/events/granted-dataset-access/v1", GrantedDatasetAccess.class);
        m.put("dataset/events/published-dataset-version/v1", PublishedDatasetVersion.class);
        m.put("dataset/events/pushed-data/v1", PushedData.class);
        m.put("dataset/events/revoked-dataset-access/v1", RevokedDatasetAccess.class);

        m.put("dataset/queries/get-data/v1", GetData.class);
        m.put("dataset/queries/get-details/v1", GetDetails.class);
        m.put("dataset/queries/get-version-details/v1", GetVersionDetails.class);

        m.put("dataset/results/get-data/v1", GetDataResult.class);
        m.put("dataset/results/get-details/v1", GetDetailsResult.class);
        m.put("dataset/results/get-version-details/v1", GetVersionDetailsResult.class);

        return m;
    }

}
