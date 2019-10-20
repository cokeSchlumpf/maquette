package maquette.controller.domain.entities.project.states;

import akka.persistence.typed.javadsl.Effect;
import maquette.controller.domain.entities.project.protocol.ProjectEvent;
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
import maquette.controller.domain.entities.project.protocol.queries.GetProjectInfo;

public interface State {

    Effect<ProjectEvent, State> onChangeProjectDescription(ChangeProjectDescription change);

    State onChangedProjectDescription(ChangedProjectDescription changed);

    Effect<ProjectEvent, State> onChangeProjectOwner(ChangeProjectOwner change);

    State onChangedProjectOwner(ChangedProjectOwner changed);

    Effect<ProjectEvent, State> onChangeProjectPrivacy(ChangeProjectPrivacy change);

    State onChangedProjectPrivacy(ChangedProjectPrivacy changed);

    Effect<ProjectEvent, State> onDeleteProject(DeleteProject deleteProject);

    State onDeletedProject(DeletedProject deletedProject);

    Effect<ProjectEvent, State> onCreateProject(CreateProject create);

    State onCreatedProject(CreatedProject created);

    Effect<ProjectEvent, State> onGetProjectDetails(GetProjectDetails get);

    Effect<ProjectEvent, State> onGetProjectInfo(GetProjectInfo get);

    Effect<ProjectEvent, State> onGrantProjectAccess(GrantProjectAccess grant);

    State onGrantedProjectAccess(GrantedProjectAccess granted);

    Effect<ProjectEvent, State> onRegisterDataset(RegisterDataset register);

    State onRegisteredDataset(RegisteredDataset registered);

    Effect<ProjectEvent, State> onRemoveDataset(RemoveDataset remove);

    State onRemovedDataset(RemovedDataset removed);

    Effect<ProjectEvent, State> onRevokeProjectAccess(RevokeProjectAccess revoke);

    State onRevokedProjectAccess(RevokedProjectAccess revoked);

}
