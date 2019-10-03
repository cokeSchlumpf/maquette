package maquette.controller.domain.entities.project;

import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.cluster.sharding.typed.javadsl.EventSourcedEntity;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
import maquette.controller.domain.entities.project.protocol.ProjectEvent;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectDescription;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectPrivacy;
import maquette.controller.domain.entities.project.protocol.commands.CreateProject;
import maquette.controller.domain.entities.project.protocol.commands.DeleteProject;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectDescription;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectPrivacy;
import maquette.controller.domain.entities.project.protocol.events.CreatedProject;
import maquette.controller.domain.entities.project.protocol.events.DeletedProject;
import maquette.controller.domain.entities.project.protocol.queries.GetProjectProperties;
import maquette.controller.domain.entities.project.states.State;
import maquette.controller.domain.entities.project.states.UninitializedProject;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.util.databind.ObjectMapperFactory;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.UserId;

public final class Project extends EventSourcedEntity<ProjectMessage, ProjectEvent, State> {

    public static EntityTypeKey<ProjectMessage> ENTITY_KEY = EntityTypeKey.create(ProjectMessage.class, "project");

    private Project(String entityId) {
        super(ENTITY_KEY, entityId);
    }

    public static EventSourcedEntity<ProjectMessage, ProjectEvent, State> create(ResourceName name) {

        String entityId = createEntityId(name);
        return new Project(entityId);
    }

    public static String createEntityId(ResourceName namespaceName) {
        return namespaceName.getValue();
    }

    public static ResourceName fromEntityId(String entityId) {
        return Operators.suppressExceptions(() -> ResourceName.apply(entityId));
    }

    @Override
    public State emptyState() {
        return UninitializedProject.apply(Effect());
    }

    @Override
    public CommandHandler<ProjectMessage, ProjectEvent, State> commandHandler() {
        return newCommandHandlerBuilder()
            .forAnyState()
            .onCommand(ChangeProjectDescription.class, State::onChangeProjectDescription)
            .onCommand(ChangeProjectPrivacy.class, State::onChangeProjectPrivacy)
            .onCommand(CreateProject.class, State::onCreateProject)
            .onCommand(DeleteProject.class, State::onDeleteProject)
            .onCommand(GetProjectProperties.class, State::onGetProjectProperties)
            .build();
    }

    @Override
    public EventHandler<State, ProjectEvent> eventHandler() {
        return newEventHandlerBuilder()
            .forAnyState()
            .onEvent(ChangedProjectDescription.class, State::onChangedProjectDescription)
            .onEvent(ChangedProjectPrivacy.class, State::onChangedProjectPrivacy)
            .onEvent(CreatedProject.class, State::onCreatedProject)
            .onEvent(DeletedProject.class, State::onDeletedProject)
            .build();
    }

}
