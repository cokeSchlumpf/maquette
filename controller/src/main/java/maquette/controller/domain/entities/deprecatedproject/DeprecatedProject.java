package maquette.controller.domain.entities.deprecatedproject;

import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.cluster.sharding.typed.javadsl.EventSourcedEntity;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
import maquette.controller.domain.entities.deprecatedproject.protocol.ProjectEvent;
import maquette.controller.domain.entities.deprecatedproject.protocol.ProjectMessage;
import maquette.controller.domain.entities.deprecatedproject.protocol.commands.ChangeProjectDescription;
import maquette.controller.domain.entities.deprecatedproject.protocol.commands.ChangeProjectPrivacy;
import maquette.controller.domain.entities.deprecatedproject.protocol.commands.CreateProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.commands.DeleteProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.events.ChangedProjectDescription;
import maquette.controller.domain.entities.deprecatedproject.protocol.events.ChangedProjectPrivacy;
import maquette.controller.domain.entities.deprecatedproject.protocol.events.CreatedProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.events.DeletedProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.queries.GetProjectProperties;
import maquette.controller.domain.entities.deprecatedproject.states.State;
import maquette.controller.domain.entities.deprecatedproject.states.UninitializedProject;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.core.ResourceName;

@Deprecated
public final class DeprecatedProject extends EventSourcedEntity<ProjectMessage, ProjectEvent, State> {

    public static EntityTypeKey<ProjectMessage> ENTITY_KEY = EntityTypeKey.create(ProjectMessage.class, "project");

    private DeprecatedProject(String entityId) {
        super(ENTITY_KEY, entityId);
    }

    public static EventSourcedEntity<ProjectMessage, ProjectEvent, State> create(ResourceName name) {

        String entityId = createEntityId(name);
        return new DeprecatedProject(entityId);
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
