package maquette.controller.domain.entities.deprecatedproject.states;

import java.time.Instant;

import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectFactories;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.deprecatedproject.protocol.ProjectEvent;
import maquette.controller.domain.entities.deprecatedproject.protocol.commands.ChangeProjectDescription;
import maquette.controller.domain.entities.deprecatedproject.protocol.commands.ChangeProjectPrivacy;
import maquette.controller.domain.entities.deprecatedproject.protocol.commands.CreateProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.commands.DeleteProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.events.ChangedProjectDescription;
import maquette.controller.domain.entities.deprecatedproject.protocol.events.ChangedProjectPrivacy;
import maquette.controller.domain.entities.deprecatedproject.protocol.events.CreatedProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.events.DeletedProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.queries.GetProjectProperties;
import maquette.controller.domain.values.namespace.ProjectDoesNotExist;
import maquette.controller.domain.values.project.ProjectProperties;

@Value
@AllArgsConstructor(staticName = "apply")
public final class UninitializedProject implements State {

    private final EffectFactories<ProjectEvent, State> effect;

    @Override
    public Effect<ProjectEvent, State> onChangeProjectDescription(ChangeProjectDescription change) {
        change.getErrorTo().tell(ProjectDoesNotExist.apply(change.getName()));
        return effect.none();
    }

    @Override
    public State onChangedProjectDescription(ChangedProjectDescription description) {
        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onChangeProjectPrivacy(ChangeProjectPrivacy change) {
        change.getErrorTo().tell(ProjectDoesNotExist.apply(change.getName()));
        return effect.none();
    }

    @Override
    public State onChangedProjectPrivacy(ChangedProjectPrivacy changed) {
        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onCreateProject(CreateProject create) {
        CreatedProject created = CreatedProject.apply(
            create.getName(), create.getDescription(), create.isPrivate(), create.getExecutor().getUserId(), Instant.now());

        return effect
            .persist(created)
            .thenRun(() -> create.getReplyTo().tell(created));
    }

    @Override
    public State onCreatedProject(CreatedProject created) {
        return ActiveProject.apply(
            effect,
            ProjectProperties.apply(created.getProject(),
                                    created.isPrivate(), created.getDescription()));
    }

    @Override
    public Effect<ProjectEvent, State> onDeleteProject(DeleteProject delete) {
        DeletedProject deleted = DeletedProject.apply(delete.getName(), delete.getExecutor().getUserId(), Instant.now());
        delete.getReplyTo().tell(deleted);
        return effect.none();
    }

    @Override
    public State onDeletedProject(DeletedProject deleted) {
        return this;
    }

    @Override
    public Effect<ProjectEvent, State> onGetProjectProperties(GetProjectProperties get) {
        get.getErrorTo().tell(ProjectDoesNotExist.apply(get.getProject()));
        return effect.none();
    }

}
