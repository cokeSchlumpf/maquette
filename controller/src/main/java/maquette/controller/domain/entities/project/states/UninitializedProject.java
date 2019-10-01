package maquette.controller.domain.entities.project.states;

import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectFactories;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.entities.project.protocol.ProjectEvent;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectDescription;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectPrivacy;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectDescription;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectPrivacy;
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
    public Effect<ProjectEvent, State> onGetProjectProperties(GetProjectProperties get) {
        get.getErrorTo().tell(ProjectDoesNotExist.apply(get.getProject()));
        return effect.none();
    }

}
