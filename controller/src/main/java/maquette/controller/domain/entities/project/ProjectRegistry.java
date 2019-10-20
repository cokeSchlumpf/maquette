package maquette.controller.domain.entities.project;

import java.time.Instant;
import java.util.Set;

import com.google.common.collect.Sets;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.typed.SingletonActor;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EventHandler;
import akka.persistence.typed.javadsl.EventSourcedBehavior;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.controller.domain.entities.project.protocol.ProjectsEvent;
import maquette.controller.domain.entities.project.protocol.ProjectsMessage;
import maquette.controller.domain.entities.project.protocol.commands.CreateProject;
import maquette.controller.domain.entities.project.protocol.commands.DeleteProject;
import maquette.controller.domain.entities.project.protocol.events.CreatedProject;
import maquette.controller.domain.entities.project.protocol.events.DeletedProject;
import maquette.controller.domain.entities.project.protocol.queries.ListProjects;
import maquette.controller.domain.entities.project.protocol.results.ListProjectsResult;
import maquette.controller.domain.values.core.ResourceName;

public final class ProjectRegistry extends EventSourcedBehavior<ProjectsMessage, ProjectsEvent, ProjectRegistry.State> {

    private static final String PERSISTENCE_ID = "project-registry";

    private ProjectRegistry() {
        super(PersistenceId.apply(PERSISTENCE_ID));
    }

    public static SingletonActor<ProjectsMessage> create() {
        Behavior<ProjectsMessage> behavior = Behaviors.setup(actor -> new ProjectRegistry());
        return SingletonActor.apply(behavior, PERSISTENCE_ID);
    }

    @Override
    public State emptyState() {
        return State.apply();
    }

    @Override
    public CommandHandler<ProjectsMessage, ProjectsEvent, State> commandHandler() {
        return newCommandHandlerBuilder()
            .forAnyState()
            .onCommand(CreateProject.class, this::onCreateProject)
            .onCommand(DeleteProject.class, this::onDeleteProject)
            .onCommand(ListProjects.class, this::onListProjects)
            .build();
    }

    @Override
    public EventHandler<State, ProjectsEvent> eventHandler() {
        return newEventHandlerBuilder()
            .forAnyState()
            .onEvent(CreatedProject.class, this::onCreatedProject)
            .onEvent(DeletedProject.class, this::onDeletedProject)
            .build();
    }

    private Effect<ProjectsEvent, State> onCreateProject(State state, CreateProject create) {
        CreatedProject created = CreatedProject.apply(create.getName(), create.getExecutor().getUserId(), Instant.now());

        if (state.getProjects().contains(create.getName())) {
            create.getReplyTo().tell(created);
            return Effect().none();
        } else {
            return Effect()
                .persist(created)
                .thenRun(() -> create.getReplyTo().tell(created));
        }
    }

    private State onCreatedProject(State state, CreatedProject created) {
        state.getProjects().add(created.getNamespace());
        return state;
    }

    @SuppressWarnings("unused")
    private Effect<ProjectsEvent, State> onDeleteProject(State state, DeleteProject deleteProject) {
        DeletedProject deleted = DeletedProject.apply(
            deleteProject.getProject(),
            deleteProject.getExecutor().getUserId(),
            Instant.now());

        return Effect()
            .persist(deleted)
            .thenRun(() -> deleteProject.getReplyTo().tell(deleted));
    }

    private State onDeletedProject(State state, DeletedProject deletedProject) {
        state.getProjects().remove(deletedProject.getName());
        return state;
    }

    private Effect<ProjectsEvent, State> onListProjects(State state, ListProjects list) {
        list.getReplyTo().tell(ListProjectsResult.apply(
            state.getProjects()));
        return Effect().none();
    }

    @Getter
    @AllArgsConstructor(staticName = "apply")
    public static class State {

        private Set<ResourceName> projects;

        public static State apply() {
            return apply(Sets.newHashSet());
        }

    }

}
