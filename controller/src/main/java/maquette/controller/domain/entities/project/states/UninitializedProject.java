package maquette.controller.domain.entities.project.states;

import java.time.Instant;

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
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.UserAuthorization;
import maquette.controller.domain.values.project.ProjectACL;
import maquette.controller.domain.values.project.ProjectDetails;
import maquette.controller.domain.values.project.ProjectDoesNotExist;

@AllArgsConstructor(staticName = "apply")
public final class UninitializedProject implements State {

    private final ActorContext<ProjectMessage> actor;

    private final EffectFactories<ProjectEvent, State> effect;

    private final DeletedProject deleted;

    public static UninitializedProject apply(
        ActorContext<ProjectMessage> actor,
        EffectFactories<ProjectEvent, State> effect) {

        return apply(actor, effect, null);
    }

    @Override
    public Effect<ProjectEvent, State> onChangeProjectDescription(ChangeProjectDescription change) {
        change.getErrorTo().tell(ProjectDoesNotExist.apply(change.getProject()));
        return effect.none();
    }

    @Override
    public State onChangedProjectDescription(ChangedProjectDescription changed) {
        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onChangeProjectOwner(ChangeProjectOwner change) {
        change.getErrorTo().tell(ProjectDoesNotExist.apply(change.getProject()));
        return effect.none();
    }

    @Override
    public State onChangedProjectOwner(ChangedProjectOwner changed) {
        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onChangeProjectPrivacy(ChangeProjectPrivacy change) {
        change.getErrorTo().tell(ProjectDoesNotExist.apply(change.getProject()));
        return effect.none();
    }

    @Override
    public State onChangedProjectPrivacy(ChangedProjectPrivacy changed) {
        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onDeleteProject(DeleteProject deleteProject) {
        if (deleted != null) {
            deleteProject.getReplyTo().tell(deleted);
        } else {
            deleteProject.getReplyTo().tell(DeletedProject.apply(
                deleteProject.getProject(),
                deleteProject.getExecutor().getUserId(),
                Instant.now()));
        }

        return effect.none();
    }

    @Override
    public State onDeletedProject(DeletedProject deletedProject) {
        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onCreateProject(CreateProject create) {
        CreatedProject created = CreatedProject.apply(create.getName(), create.getDescription(), create.isPrivate(), create.getExecutor().getUserId(), Instant.now());

        return effect
            .persist(created)
            .thenRun(() -> create.getReplyTo().tell(created));
    }

    @Override
    public State onCreatedProject(CreatedProject created) {
        final GrantedAuthorization owner = GrantedAuthorization.apply(
            created.getCreatedBy(),
            created.getCreatedAt(),
            UserAuthorization.apply(created.getCreatedBy()));

        ProjectDetails details = ProjectDetails.apply(
            created.getName(),
            created.getCreatedBy(),
            created.getCreatedAt(),
            created.getDescription(),
            created.getCreatedBy(),
            created.getCreatedAt(),
            ProjectACL.apply(owner, Sets.newHashSet(), created.isPrivate()),
            Sets.newHashSet());

        return ActiveProject.apply(actor, effect, details);
    }

    @Override
    public Effect<ProjectEvent, State> onGetProjectDetails(GetProjectDetails get) {
        get.getErrorTo().tell(ProjectDoesNotExist.apply(get.getProject()));
        return effect.none();
    }

    @Override
    public Effect<ProjectEvent, State> onGrantProjectAccess(GrantProjectAccess grant) {
        grant.getErrorTo().tell(ProjectDoesNotExist.apply(grant.getProject()));
        return effect.none();
    }

    @Override
    public State onGrantedProjectAccess(GrantedProjectAccess granted) {
        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onRegisterDataset(RegisterDataset register) {
        register.getErrorTo().tell(ProjectDoesNotExist.apply(register.getProject()));
        return effect.none();
    }

    @Override
    public State onRegisteredDataset(RegisteredDataset registered) {
        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onRemoveDataset(RemoveDataset remove) {
        remove.getErrorTo().tell(ProjectDoesNotExist.apply(remove.getProject()));
        return effect.none();
    }

    @Override
    public State onRemovedDataset(RemovedDataset removed) {
        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onRevokeProjectAccess(RevokeProjectAccess revoke) {
        revoke.getErrorTo().tell(ProjectDoesNotExist.apply(revoke.getProject()));
        return effect.none();
    }

    @Override
    public State onRevokedProjectAccess(RevokedProjectAccess revoked) {
        return this;
    }

}
