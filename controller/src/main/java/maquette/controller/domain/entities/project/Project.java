package maquette.controller.domain.entities.project;

import akka.actor.typed.javadsl.ActorContext;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.cluster.sharding.typed.javadsl.EventSourcedEntity;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
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
import maquette.controller.domain.entities.project.states.State;
import maquette.controller.domain.entities.project.states.UninitializedProject;
import maquette.controller.domain.values.core.ResourceName;

public final class Project extends EventSourcedEntity<ProjectMessage, ProjectEvent, State> {

    public static EntityTypeKey<ProjectMessage> ENTITY_KEY = EntityTypeKey.create(ProjectMessage.class, "project");

    private final ActorContext<ProjectMessage> actor;

    private Project(String entityId, ActorContext<ProjectMessage> actor) {
        super(ENTITY_KEY, entityId);
        this.actor = actor;
    }

    public static EventSourcedEntity<ProjectMessage, ProjectEvent, State> create(
        ActorContext<ProjectMessage> actor, ResourceName name) {

        String entityId = createEntityId(name);
        return new Project(entityId, actor);
    }

    public static String createEntityId(ResourceName namespaceName) {
        return namespaceName.getValue();
    }

    @Override
    public State emptyState() {
        return UninitializedProject.apply(actor, Effect());
    }

    @Override
    public CommandHandler<ProjectMessage, ProjectEvent, State> commandHandler() {
        return newCommandHandlerBuilder()
            .forAnyState()
            .onCommand(ChangeProjectDescription.class, State::onChangeProjectDescription)
            .onCommand(ChangeProjectOwner.class, State::onChangeProjectOwner)
            .onCommand(ChangeProjectPrivacy.class, State::onChangeProjectPrivacy)
            .onCommand(CreateProject.class, State::onCreateProject)
            .onCommand(DeleteProject.class, State::onDeleteProject)
            .onCommand(GetProjectDetails.class, State::onGetProjectDetails)
            .onCommand(GrantProjectAccess.class, State::onGrantProjectAccess)
            .onCommand(RegisterDataset.class, State::onRegisterDataset)
            .onCommand(RemoveDataset.class, State::onRemoveDataset)
            .onCommand(RevokeProjectAccess.class, State::onRevokeProjectAccess)
            .build();
    }

    @Override
    public EventHandler<State, ProjectEvent> eventHandler() {
        return newEventHandlerBuilder()
            .forAnyState()
            .onEvent(ChangedProjectDescription.class, State::onChangedProjectDescription)
            .onEvent(ChangedProjectOwner.class, State::onChangedProjectOwner)
            .onEvent(ChangedProjectPrivacy.class, State::onChangedProjectPrivacy)
            .onEvent(CreatedProject.class, State::onCreatedProject)
            .onEvent(DeletedProject.class, State::onDeletedProject)
            .onEvent(GrantedProjectAccess.class, State::onGrantedProjectAccess)
            .onEvent(RegisteredDataset.class, State::onRegisteredDataset)
            .onEvent(RemovedDataset.class, State::onRemovedDataset)
            .onEvent(RevokedProjectAccess.class, State::onRevokedProjectAccess)
            .build();
    }

}
