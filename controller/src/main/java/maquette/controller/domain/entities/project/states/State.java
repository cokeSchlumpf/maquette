package maquette.controller.domain.entities.project.states;

import akka.persistence.typed.javadsl.Effect;
import maquette.controller.domain.entities.project.protocol.ProjectEvent;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectDescription;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectPrivacy;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectDescription;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectPrivacy;
import maquette.controller.domain.entities.project.protocol.queries.GetProjectProperties;

public interface State {

    Effect<ProjectEvent, State> onChangeProjectDescription(ChangeProjectDescription change);

    State onChangedProjectDescription(ChangedProjectDescription description);

    Effect<ProjectEvent, State> onChangeProjectPrivacy(ChangeProjectPrivacy change);

    State onChangedProjectPrivacy(ChangedProjectPrivacy changed);

    Effect<ProjectEvent, State> onGetProjectProperties(GetProjectProperties get);

}
