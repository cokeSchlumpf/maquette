package maquette.controller.domain.entities.project.states;

import java.time.Instant;

import akka.cluster.ddata.Replicator;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectFactories;
import lombok.AllArgsConstructor;
import lombok.Value;
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
import maquette.controller.domain.values.namespace.ProjectDoesNotExist;

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
            create.getName(), create.getProperties(), create.getExecutor().getUserId(), Instant.now());

        return effect
            .persist(created)
            .thenRun(() -> create.getReplyTo().tell(created));
    }

    @Override
    public State onCreatedProject(CreatedProject created) {
        return ActiveProject.apply(effect, created.getProperties());
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
