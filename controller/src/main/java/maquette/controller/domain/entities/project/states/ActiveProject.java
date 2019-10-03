package maquette.controller.domain.entities.project.states;

import java.time.Instant;

import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectFactories;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.project.protocol.ProjectEvent;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectDescription;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectPrivacy;
import maquette.controller.domain.entities.project.protocol.commands.CreateProject;
import maquette.controller.domain.entities.project.protocol.commands.DeleteProject;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectDescription;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectPrivacy;
import maquette.controller.domain.entities.project.protocol.events.CreatedProject;
import maquette.controller.domain.entities.project.protocol.events.DeletedProject;
import maquette.controller.domain.entities.project.protocol.queries.GetProjectProperties;
import maquette.controller.domain.entities.project.protocol.results.GetProjectPropertiesResult;
import maquette.controller.domain.values.project.ProjectProperties;

@AllArgsConstructor(staticName = "apply")
public final class ActiveProject implements State {

    private final EffectFactories<ProjectEvent, State> effect;

    private ProjectProperties properties;

    @Override
    public Effect<ProjectEvent, State> onChangeProjectDescription(ChangeProjectDescription change) {
        ChangedProjectDescription changed = ChangedProjectDescription.apply(
            properties.getName(), change.getDescription(), change.getExecutor().getUserId(), Instant.now());

        if (properties.getDescription().isPresent() && properties.getDescription().get().equals(change.getDescription())) {
            change.getReplyTo().tell(changed);
            return effect.none();
        } else {
            return effect
                .persist(changed)
                .thenRun(() -> change.getReplyTo().tell(changed));
        }
    }

    @Override
    public State onChangedProjectDescription(ChangedProjectDescription description) {
        this.properties = properties.withDescription(description.getDescription());
        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onChangeProjectPrivacy(ChangeProjectPrivacy change) {
        ChangedProjectPrivacy changed =  ChangedProjectPrivacy.apply(
            properties.getName(), change.isPrivate(), change.getExecutor().getUserId(), Instant.now());

        if (Boolean.valueOf(properties.isPrivate()).equals(change.isPrivate())) {
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
        this.properties = properties.withPrivate(changed.isPrivate());
        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onCreateProject(CreateProject create) {
        CreatedProject created = CreatedProject.apply(
            create.getName(), create.getProperties(), create.getExecutor().getUserId(), Instant.now());

        create.getReplyTo().tell(created);

        return effect.none();
    }

    @Override
    public State onCreatedProject(CreatedProject created) {
        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onDeleteProject(DeleteProject delete) {
        DeletedProject deleted = DeletedProject.apply(delete.getName(), delete.getExecutor().getUserId(), Instant.now());

        return effect
            .persist(deleted)
            .thenRun(() -> delete.getReplyTo().tell(deleted));
    }

    @Override
    public State onDeletedProject(DeletedProject deleted) {
        return UninitializedProject.apply(effect);
    }

    @Override
    public Effect<ProjectEvent, State> onGetProjectProperties(GetProjectProperties get) {
        get.getReplyTo().tell(GetProjectPropertiesResult.apply(properties));
        return effect.none();
    }

}
