package maquette.controller.domain.entities.dataset.states;

import java.time.Instant;

import com.google.common.collect.Sets;

import akka.actor.typed.javadsl.ActorContext;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectFactories;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.protocol.DatasetEvent;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.commands.CreateDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.DeleteDataset;
import maquette.controller.domain.entities.dataset.protocol.commands.GrantDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.commands.RevokeDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.CreatedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.DeletedDataset;
import maquette.controller.domain.entities.dataset.protocol.events.GrantedDatasetAccess;
import maquette.controller.domain.entities.dataset.protocol.events.RevokedDatasetAccess;
import maquette.controller.domain.values.dataset.DatasetACL;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.UserAuthorization;

@AllArgsConstructor(staticName = "apply")
public class UninitializedDataset implements State {

    private final ActorContext<DatasetMessage> actor;

    private final EffectFactories<DatasetEvent, State> effect;

    private final DeletedDataset deleted;

    public static UninitializedDataset apply(
        ActorContext<DatasetMessage> actor,
        EffectFactories<DatasetEvent, State> effect) {

        return apply(actor, effect, null);
    }

    @Override
    public Effect<DatasetEvent, State> onCreateDataset(CreateDataset create) {
        CreatedDataset created = CreatedDataset.apply(
            create.getDataset(),
            create.getExecutor().getUserId(),
            Instant.now());

        return effect
            .persist(created)
            .thenRun(() -> create.getReplyTo().tell(created));
    }

    @Override
    public State onCreatedDataset(CreatedDataset created) {
        final GrantedAuthorization granted = GrantedAuthorization.apply(
            created.getCreatedBy(),
            Instant.now(),
            UserAuthorization.apply(created.getCreatedBy().getName()));

        final DatasetDetails details = DatasetDetails.apply(
            created.getDataset(),
            Instant.now(),
            created.getCreatedBy(),
            Instant.now(),
            created.getCreatedBy(),
            Sets.newHashSet(),
            DatasetACL.apply(granted, Sets.newHashSet()));

        return ActiveDataset.apply(actor, effect, details);
    }

    @Override
    public Effect<DatasetEvent, State> onDeleteDataset(DeleteDataset delete) {
        if (deleted == null) {
            DeletedDataset deleted = DeletedDataset.apply(delete.getDataset(), Instant.now(), delete.getExecutor().getUserId());
            delete.getReplyTo().tell(deleted);

            return effect.none();
        } else {
            delete.getReplyTo().tell(deleted);
            return effect.none();
        }
    }

    @Override
    public State onDeletedDataset(DeletedDataset deleted) {
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onGrantDatasetAccess(GrantDatasetAccess grant) {
        return effect.none();
    }

    @Override
    public State onGrantedDatasetAccess(GrantedDatasetAccess granted) {
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onRevokeDatasetAccess(RevokeDatasetAccess revoke) {
        return effect.none();
    }

    @Override
    public State onRevokedDatasetAccess(RevokedDatasetAccess revoked) {
        return this;
    }

}
