package maquette.controller.domain.entities.deprecatedproject.states;

import akka.persistence.typed.javadsl.Effect;
import maquette.controller.domain.entities.deprecatedproject.protocol.ProjectEvent;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectDescription;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectPrivacy;
import maquette.controller.domain.entities.deprecatedproject.protocol.commands.CreateProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.commands.DeleteProject;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectDescription;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectPrivacy;
import maquette.controller.domain.entities.deprecatedproject.protocol.events.CreatedProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.events.DeletedProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.queries.GetProjectProperties;

public interface State {

    Effect<ProjectEvent, State> onChangeProjectDescription(ChangeProjectDescription change);

    State onChangedProjectDescription(ChangedProjectDescription description);

    Effect<ProjectEvent, State> onChangeProjectPrivacy(ChangeProjectPrivacy change);

    State onChangedProjectPrivacy(ChangedProjectPrivacy changed);

    Effect<ProjectEvent, State> onCreateProject(CreateProject create);

    State onCreatedProject(CreatedProject created);

    Effect<ProjectEvent, State> onDeleteProject(DeleteProject delete);

    State onDeletedProject(DeletedProject deleted);

    Effect<ProjectEvent, State> onGetProjectProperties(GetProjectProperties get);

}
