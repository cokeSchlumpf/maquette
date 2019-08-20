package maquette.controller.domain.entities.dataset.protocol;

import java.util.Map;

import com.google.common.collect.Maps;

import akka.actor.ExtendedActorSystem;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.DeleteDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.GrantDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PushData;
import maquette.controller.domain.entities.dataset.protocol.commands.RevokeDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.DeletedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.GrantedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.PublishedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.PushedData;
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.queries.GetData;
import maquette.controller.domain.entities.dataset.protocol.queries.IsAllowedToConsume;
import maquette.controller.domain.entities.dataset.protocol.queries.IsAllowedToProduce;
import maquette.controller.domain.entities.dataset.protocol.results.BooleanResult;
import maquette.controller.domain.entities.dataset.protocol.results.GetDataResult;
import maquette.controller.domain.entities.namespace.protocol.commands.ChangeOwner;
import maquette.controller.domain.entities.namespace.protocol.events.ChangedOwner;
import maquette.controller.domain.util.databind.AbstractMessageSerializer;

public final class MessageSerializer extends AbstractMessageSerializer {

    protected MessageSerializer(ExtendedActorSystem actorSystem) {
        super(actorSystem, 2403);
    }

    @Override
    protected Map<String, Class<?>> getManifestToClass() {
        Map<String, Class<?>> m = Maps.newHashMap();

        m.put("dataset/commands/create-dataset/v1", CreateDataset.class);
        m.put("dataset/commands/create-dataset-version/v1", CreateDatasetVersion.class);
        m.put("dataset/commands/delete-dataset/v1", DeleteDataset.class);
        m.put("dataset/commands/grant-dataset-access/v1", GrantDatasetAccess.class);
        m.put("dataset/commands/publish-dataset-version/v1", PublishDatasetVersion.class);
        m.put("dataset/commands/push-data/v1", PushData.class);
        m.put("dataset/commands/revoke-dataset-access/v1", RevokeDatasetAccess.class);

        m.put("dataset/events/created-dataset/v1", CreatedDataset.class);
        m.put("dataset/events/created-dataset-version/v1", CreatedDatasetVersion.class);
        m.put("dataset/events/deleted-dataset/v1", DeletedDataset.class);
        m.put("dataset/events/granted-dataset-access/v1", GrantedDatasetAccess.class);
        m.put("dataset/events/published-dataset-version/v1", PublishedDatasetVersion.class);
        m.put("dataset/events/pushed-data/v1", PushedData.class);
        m.put("dataset/events/revoked-dataset-access/v1", RevokedDatasetAccess.class);

        m.put("dataset/queries/get-data/v1", GetData.class);
        m.put("dataset/queries/is-allowed-to-consume/v1", IsAllowedToConsume.class);
        m.put("dataset/queries/is-allowed-to-produce/v1", IsAllowedToProduce.class);

        m.put("dataset/results/get-data/v1", GetDataResult.class);
        m.put("dataset/results/boolean/v1", BooleanResult.class);

        return m;
    }

}
