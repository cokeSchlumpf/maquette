package maquette.controller.domain.entities.dataset.states;

import akka.persistence.typed.javadsl.Effect;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.entities.dataset.protocol.commands.ApproveDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeDatasetDescription;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeDatasetGovernance;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeDatasetPrivacy;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.DeleteDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.GrantDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishCommittedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PushData;
import maquette.controller.domain.entities.dataset.protocol.commands.RejectDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.commands.RevokeDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.commands.RevokeDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.events.ApprovedDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedDatasetDescription;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedDatasetGovernance;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedDatasetPrivacy;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.DeletedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.GrantedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.PublishedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.RejectedDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccessRequest;
import maquette.controller.domain.entities.dataset.protocol.queries.GetAllVersions;
import maquette.controller.domain.entities.dataset.protocol.queries.GetData;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.protocol.queries.GetVersionDetails;

public interface State {

    Effect<DatasetEvent, State> onApproveDatasetAccessRequest(ApproveDatasetAccessRequest approve);

    State onApprovedDatasetAccessRequest(ApprovedDatasetAccessRequest approved);

    Effect<DatasetEvent, State> onChangeDatasetDescription(ChangeDatasetDescription change);

    State onChangedDatasetDescription(ChangedDatasetDescription changed);

    Effect<DatasetEvent, State> onChangeDatasetGovernance(ChangeDatasetGovernance change);

    State onChangedDatasetGovernance(ChangedDatasetGovernance changed);

    Effect<DatasetEvent, State> onChangeDatasetPrivacy(ChangeDatasetPrivacy change);

    State onChangedDatasetPrivacy(ChangedDatasetPrivacy changed);

    Effect<DatasetEvent, State> onChangeOwner(ChangeOwner change);

    State onChangedOwner(ChangedOwner changed);

    Effect<DatasetEvent, State> onCreateDataset(CreateDataset create);

    State onCreatedDataset(CreatedDataset created);

    Effect<DatasetEvent, State> onCreateDatasetAccessRequest(CreateDatasetAccessRequest create);

    State onCreatedDatasetAccessRequest(CreatedDatasetAccessRequest created);

    Effect<DatasetEvent, State> onCreateDatasetVersion(CreateDatasetVersion create);

    State onCreatedDatasetVersion(CreatedDatasetVersion created);

    Effect<DatasetEvent, State> onDeleteDataset(DeleteDataset delete);

    State onDeletedDataset(DeletedDataset deleted);

    Effect<DatasetEvent, State> onGetAllVersions(GetAllVersions get);

    Effect<DatasetEvent, State> onGetData(GetData get);

    Effect<DatasetEvent, State> onGetDetails(GetDetails get);

    Effect<DatasetEvent, State> onGetVersionDetails(GetVersionDetails get);

    Effect<DatasetEvent, State> onGrantDatasetAccess(GrantDatasetAccess grant);

    State onGrantedDatasetAccess(GrantedDatasetAccess granted);

    Effect<DatasetEvent, State> onPublishCommittedDatasetVersion(PublishCommittedDatasetVersion publish);

    Effect<DatasetEvent, State> onPublishDatasetVersion(PublishDatasetVersion publish);

    State onPublishedDatasetVersion(PublishedDatasetVersion published);

    Effect<DatasetEvent, State> onPushData(PushData push);

    Effect<DatasetEvent, State> onRejectDatasetAccessRequest(RejectDatasetAccessRequest reject);

    State onRejectedDatasetAccessRequest(RejectedDatasetAccessRequest rejected);

    Effect<DatasetEvent, State> onRevokeDatasetAccess(RevokeDatasetAccess revoke);

    State onRevokedDatasetAccess(RevokedDatasetAccess revoked);

    Effect<DatasetEvent, State> onRevokeDatasetAccessRequest(RevokeDatasetAccessRequest revoke);

    State onRevokedDatasetAccessRequest(RevokedDatasetAccessRequest revoked);

}
