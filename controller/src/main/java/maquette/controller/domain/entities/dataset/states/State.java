package maquette.controller.domain.entities.dataset.states;

import akka.persistence.typed.javadsl.Effect;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.entities.dataset.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.DeleteDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.GrantDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishCommittedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PushData;
import maquette.controller.domain.entities.dataset.protocol.commands.RevokeDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.ChangedOwner;
import maquette.controller.domain.entities.dataset.protocol.events.CommittedDatasetVersion;
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

public interface State {

    Effect<DatasetEvent, State> onChangeOwner(ChangeOwner change);

    State onChangedOwner(ChangedOwner changed);

    Effect<DatasetEvent, State> onCreateDataset(CreateDataset create);

    State onCreatedDataset(CreatedDataset created);

    Effect<DatasetEvent, State> onCreateDatasetVersion(CreateDatasetVersion create);

    State onCreatedDatasetVersion(CreatedDatasetVersion created);

    Effect<DatasetEvent, State> onDeleteDataset(DeleteDataset delete);

    State onDeletedDataset(DeletedDataset deleted);

    Effect<DatasetEvent, State> onGetData(GetData get);

    Effect<DatasetEvent, State> onGetDetails(GetDetails get);

    Effect<DatasetEvent, State> onGetVersionDetails(GetVersionDetails get);

    Effect<DatasetEvent, State> onGrantDatasetAccess(GrantDatasetAccess grant);

    State onGrantedDatasetAccess(GrantedDatasetAccess granted);

    Effect<DatasetEvent, State> onPublishCommittedDatasetVersion(PublishCommittedDatasetVersion publish);

    Effect<DatasetEvent, State> onPublishDatasetVersion(PublishDatasetVersion publish);

    State onPublishedDatasetVersion(PublishedDatasetVersion published);

    Effect<DatasetEvent, State> onPushData(PushData push);

    Effect<DatasetEvent, State> onRevokeDatasetAccess(RevokeDatasetAccess revoke);

    State onRevokedDatasetAccess(RevokedDatasetAccess revoked);

}
