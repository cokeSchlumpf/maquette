package maquette.controller.domain.entities.dataset.states;

import java.time.Instant;
import java.util.Optional;

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
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.protocol.results.GetDetailsResult;
import maquette.controller.domain.values.dataset.DatasetACL;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.DatasetGrant;
import maquette.controller.domain.values.iam.GrantedAuthorization;

@AllArgsConstructor(staticName = "apply")
public class ActiveDataset implements State {

    private final ActorContext<DatasetMessage> actor;

    private final EffectFactories<DatasetEvent, State> effect;

    private DatasetDetails details;

    @Override
    public Effect<DatasetEvent, State> onCreateDataset(CreateDataset create) {
        CreatedDataset created = CreatedDataset.apply(details.getDataset(), details.getCreatedBy(), details.getCreated());
        create.getReplyTo().tell(created);
        return effect.none();
    }

    @Override
    public State onCreatedDataset(CreatedDataset created) {
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onDeleteDataset(DeleteDataset delete) {
        DeletedDataset deleted = DeletedDataset.apply(details.getDataset(), Instant.now(), delete.getExecutor().getUserId());
        return effect
            .persist(deleted)
            .thenRun(() -> delete.getReplyTo().tell(deleted));
    }

    @Override
    public State onDeletedDataset(DeletedDataset deleted) {
        return UninitializedDataset.apply(actor, effect, deleted);
    }

    @Override
    public Effect<DatasetEvent, State> onGetDetails(GetDetails get) {
        get.getReplyTo().tell(GetDetailsResult.apply(details));
        return effect.none();
    }

    @Override
    public Effect<DatasetEvent, State> onGrantDatasetAccess(GrantDatasetAccess grant) {
        Optional<DatasetGrant> existing = details.getAcl().findGrant(grant.getGrantFor(), grant.getGrant());

        if (existing.isPresent()) {
            GrantedDatasetAccess granted = GrantedDatasetAccess.apply(
                details.getDataset(),
                grant.getGrant(),
                existing.get().getAuthorization());

            grant.getReplyTo().tell(granted);

            return effect.none();
        } else {
            GrantedAuthorization grantedAuthorization = GrantedAuthorization.apply(
                grant.getExecutor().getUserId(),
                Instant.now(),
                grant.getGrantFor());

            GrantedDatasetAccess granted = GrantedDatasetAccess.apply(
                details.getDataset(),
                grant.getGrant(),
                grantedAuthorization);

            return effect
                .persist(granted)
                .thenRun(() -> grant.getReplyTo().tell(granted));
        }
    }

    @Override
    public State onGrantedDatasetAccess(GrantedDatasetAccess granted) {
        DatasetACL acl = details.getAcl().withGrant(granted.getGrantedFor(), granted.getGranted());
        details = details.withAcl(acl);
        return this;
    }

    @Override
    public Effect<DatasetEvent, State> onRevokeDatasetAccess(RevokeDatasetAccess revoke) {
        Optional<DatasetGrant> existing = details.getAcl().findGrant(revoke.getRevokeFrom(), revoke.getRevoke());

        if (existing.isPresent()) {
            RevokedDatasetAccess revoked = RevokedDatasetAccess.apply(
                details.getDataset(),
                revoke.getRevoke(),
                Instant.now(),
                revoke.getExecutor().getUserId(),
                existing.get().getAuthorization());

            return effect
                .persist(revoked)
                .thenRun(() -> revoke.getReplyTo().tell(revoked));
        } else {
            GrantedAuthorization grantedAuthorization = GrantedAuthorization.apply(
                revoke.getExecutor().getUserId(),
                Instant.now(),
                revoke.getRevokeFrom());

            RevokedDatasetAccess revoked = RevokedDatasetAccess.apply(
                details.getDataset(),
                revoke.getRevoke(),
                Instant.now(),
                revoke.getExecutor().getUserId(),
                grantedAuthorization);

            revoke.getReplyTo().tell(revoked);

            return effect
                .none();

        }
    }

    @Override
    public State onRevokedDatasetAccess(RevokedDatasetAccess revoked) {
        DatasetACL acl = details.getAcl().withoutGrant(revoked.getRevokedFrom().getAuthorization(), revoked.getRevoked());
        details = details.withAcl(acl);
        return this;
    }

}
