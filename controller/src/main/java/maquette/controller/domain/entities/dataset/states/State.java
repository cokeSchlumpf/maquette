package maquette.controller.domain.entities.dataset.states;

import akka.persistence.typed.javadsl.Effect;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
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

public interface State {

    Effect<DatasetEvent, State> onChangeOwner(ChangeOwner change);

    State onChangedOwner(ChangedOwner changed);

    Effect<DatasetEvent, State> onCreateDataset(CreateDataset create);

    State onCreatedDataset(CreatedDataset created);

    Effect<DatasetEvent, State> onDeleteDataset(DeleteDataset delete);

    State onDeletedDataset(DeletedDataset deleted);

    Effect<DatasetEvent, State> onGetDetails(GetDetails get);

    Effect<DatasetEvent, State> onGrantDatasetAccess(GrantDatasetAccess grant);

    State onGrantedDatasetAccess(GrantedDatasetAccess granted);

    Effect<DatasetEvent, State> onRevokeDatasetAccess(RevokeDatasetAccess revoke);

    State onRevokedDatasetAccess(RevokedDatasetAccess revoked);

}
