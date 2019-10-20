package maquette.controller.domain.entities.project.states;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;

import akka.actor.typed.javadsl.ActorContext;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectFactories;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.project.protocol.ProjectEvent;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectDescription;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectOwner;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectPrivacy;
import maquette.controller.domain.entities.project.protocol.commands.CreateProject;
import maquette.controller.domain.entities.project.protocol.commands.DeleteProject;
import maquette.controller.domain.entities.project.protocol.commands.GrantProjectAccess;
import maquette.controller.domain.entities.project.protocol.commands.RegisterDataset;
import maquette.controller.domain.entities.project.protocol.commands.RemoveDataset;
import maquette.controller.domain.entities.project.protocol.commands.RevokeProjectAccess;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectDescription;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectOwner;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectPrivacy;
import maquette.controller.domain.entities.project.protocol.events.CreatedProject;
import maquette.controller.domain.entities.project.protocol.events.DeletedProject;
import maquette.controller.domain.entities.project.protocol.events.GrantedProjectAccess;
import maquette.controller.domain.entities.project.protocol.events.RegisteredDataset;
import maquette.controller.domain.entities.project.protocol.events.RemovedDataset;
import maquette.controller.domain.entities.project.protocol.events.RevokedProjectAccess;
import maquette.controller.domain.entities.project.protocol.queries.GetProjectDetails;
import maquette.controller.domain.entities.project.protocol.queries.GetProjectInfo;
import maquette.controller.domain.entities.project.protocol.results.GetProjectDetailsResult;
import maquette.controller.domain.entities.project.protocol.results.GetProjectInfoResult;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.project.ProjectACL;
import maquette.controller.domain.values.project.ProjectDetails;
import maquette.controller.domain.values.project.ProjectGrant;
import maquette.controller.domain.values.project.ProjectInfo;

@AllArgsConstructor(staticName = "apply")
public final class ActiveProject implements State {

    private final ActorContext<ProjectMessage> actor;

    private final EffectFactories<ProjectEvent, State> effect;

    private ProjectDetails details;

    @Override
    public Effect<ProjectEvent, State> onChangeProjectDescription(ChangeProjectDescription change) {
        ChangedProjectDescription changed = ChangedProjectDescription.apply(
            change.getProject(), change.getDescription(), change.getExecutor().getUserId(), Instant.now());

        if (details.getDescription().equals(change.getDescription())) {
            change.getReplyTo().tell(changed);
            return effect.none();
        } else {
            return effect
                .persist(changed)
                .thenRun(() -> change.getReplyTo().tell(changed));
        }
    }

    @Override
    public State onChangedProjectDescription(ChangedProjectDescription changed) {
        this.details = details
            .withDescription(changed.getDescription())
            .withModified(changed.getChangedBy(), changed.getChangedAt());

        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onChangeProjectOwner(ChangeProjectOwner change) {
        if (change.getOwner().equals(details.getAcl().getOwner().getAuthorization())) {
            ChangedProjectOwner changed = ChangedProjectOwner.apply(details.getName(), details.getAcl().getOwner());
            change.getReplyTo().tell(changed);
            return effect.none();
        } else {
            GrantedAuthorization granted = GrantedAuthorization.apply(
                change.getExecutor().getUserId(),
                Instant.now(),
                change.getOwner());

            ChangedProjectOwner changedProjectOwner = ChangedProjectOwner.apply(details.getName(), granted);

            return effect
                .persist(changedProjectOwner)
                .thenRun(() -> change.getReplyTo().tell(changedProjectOwner));
        }
    }

    @Override
    public State onChangedProjectOwner(ChangedProjectOwner changed) {
        this.details = this.details
            .withAcl(this.details.getAcl().withOwner(changed.getNewOwner()))
            .withModified(changed.getNewOwner().getBy(), changed.getNewOwner().getAt());

        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onChangeProjectPrivacy(ChangeProjectPrivacy change) {
        ChangedProjectPrivacy changed = ChangedProjectPrivacy.apply(
            change.getProject(), change.isPrivate(), change.getExecutor().getUserId(), Instant.now());

        if (Boolean.valueOf(details.getAcl().isPrivate()).equals(change.isPrivate())) {
            change.getReplyTo().tell(changed);
            return effect.none();
        } else {
            return effect
                .persist(changed)
                .thenRun(() -> change.getReplyTo().tell(changed));
        }
    }

    @Override
    public State onChangedProjectPrivacy(ChangedProjectPrivacy changed) {
        ProjectACL acl = details.getAcl().withPrivacy(changed.isPrivate());
        details = details
            .withAcl(acl)
            .withModified(changed.getChangedBy(), changed.getChangedAt());

        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onDeleteProject(DeleteProject deleteProject) {
        // TODO: Remove actual data -> Must be implemented in service.

        DeletedProject deleted = DeletedProject.apply(
            deleteProject.getProject(),
            deleteProject.getExecutor().getUserId(),
            Instant.now());

        return effect
            .persist(deleted)
            .thenRun(() -> deleteProject.getReplyTo().tell(deleted));
    }

    @Override
    public State onDeletedProject(DeletedProject deletedProject) {
        return UninitializedProject.apply(actor, effect, deletedProject);
    }

    @Override
    public Effect<ProjectEvent, State> onCreateProject(CreateProject create) {
        CreatedProject created = CreatedProject.apply(details.getName(),
                                                      details.getDescription(),
                                                      create.isPrivate(),
                                                      details.getCreatedBy(),
                                                      details.getCreated());

        create.getReplyTo().tell(created);
        return effect.none();
    }

    @Override
    public State onCreatedProject(CreatedProject created) {
        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onGetProjectDetails(GetProjectDetails get) {
        get.getReplyTo().tell(GetProjectDetailsResult.apply(details));
        return effect.none();
    }

    @Override
    public Effect<ProjectEvent, State> onGetProjectInfo(GetProjectInfo get) {
        ProjectInfo info = ProjectInfo.apply(
            details.getName(),
            details.getModified(),
            details.getAcl(),
            details.getDatasets());

        GetProjectInfoResult result = GetProjectInfoResult.apply(info);
        get.getReplyTo().tell(result);

        return effect.none();
    }

    @Override
    public Effect<ProjectEvent, State> onGrantProjectAccess(GrantProjectAccess grant) {
        Optional<ProjectGrant> existingGrant = details.getAcl().findGrant(grant.getGrantFor(), grant.getGrant());

        if (existingGrant.isPresent()) {
            GrantedProjectAccess granted = GrantedProjectAccess.apply(
                details.getName(),
                existingGrant.get().getPrivilege(),
                existingGrant.get().getAuthorization());

            grant.getReplyTo().tell(granted);

            return effect.none();
        } else {
            GrantedProjectAccess granted = GrantedProjectAccess.apply(
                details.getName(),
                grant.getGrant(),
                GrantedAuthorization.apply(grant.getExecutor().getUserId(), Instant.now(), grant.getGrantFor()));

            return effect
                .persist(granted)
                .thenRun(() -> grant.getReplyTo().tell(granted));
        }
    }

    @Override
    public State onGrantedProjectAccess(GrantedProjectAccess granted) {
        ProjectACL acl = this
            .details
            .getAcl()
            .withGrant(granted.getGrantedFor(), granted.getGranted());

        this.details = this.details
            .withAcl(acl)
            .withModified(granted.getGrantedFor().getBy(), granted.getGrantedFor().getAt());

        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onRegisterDataset(RegisterDataset register) {
        RegisteredDataset registered = RegisteredDataset.apply(register.getProject(), register.getDataset());

        if (details.getDatasets().contains(register.getDataset())) {
            register.getReplyTo().tell(registered);
            return effect.none();
        } else {
            return effect
                .persist(registered)
                .thenRun(() -> register.getReplyTo().tell(registered));
        }
    }

    @Override
    public State onRegisteredDataset(RegisteredDataset registered) {
        Set<ResourceName> datasets = Sets.newHashSet(this.details.getDatasets());
        datasets.add(registered.getDataset());

        this.details = details.withDatasets(datasets);
        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onRemoveDataset(RemoveDataset remove) {
        RemovedDataset removed = RemovedDataset.apply(remove.getProject(), remove.getDataset());

        if (this.details.getDatasets().contains(remove.getDataset())) {
            return effect
                .persist(removed)
                .thenRun(() -> remove.getReplyTo().tell(removed));
        } else {
            remove.getReplyTo().tell(removed);
            return effect.none();
        }
    }

    @Override
    public State onRemovedDataset(RemovedDataset removed) {
        Set<ResourceName> datasets = Sets.newHashSet(this.details.getDatasets());
        datasets.remove(removed.getDataset());

        this.details = details.withDatasets(datasets);
        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onRevokeProjectAccess(RevokeProjectAccess revoke) {
        Optional<ProjectGrant> existingGrant = details.getAcl().findGrant(revoke.getRevokeFrom(), revoke.getRevoke());

        if (existingGrant.isPresent()) {
            RevokedProjectAccess revoked = RevokedProjectAccess.apply(
                details.getName(),
                existingGrant.get().getPrivilege(),
                existingGrant.get().getAuthorization());

            return effect
                .persist(revoked)
                .thenRun(() -> revoke.getReplyTo().tell(revoked));
        } else {
            GrantedAuthorization granted = GrantedAuthorization.apply(
                revoke.getExecutor().getUserId(),
                Instant.now(),
                revoke.getRevokeFrom());

            RevokedProjectAccess revoked = RevokedProjectAccess.apply(
                details.getName(),
                revoke.getRevoke(),
                granted);

            revoke.getReplyTo().tell(revoked);

            return effect.none();
        }
    }

    @Override
    public State onRevokedProjectAccess(RevokedProjectAccess revoked) {
        ProjectACL acl = this
            .details
            .getAcl()
            .withoutGrant(revoked.getRevokedFrom().getAuthorization(), revoked.getRevoked());

        details = details
            .withAcl(acl)
            .withModified(revoked.getRevokedFrom().getBy(), revoked.getRevokedFrom().getAt());

        return this;
    }

}
