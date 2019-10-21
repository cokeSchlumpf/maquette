package maquette.controller.domain.entities.project.protocol;

import java.util.Map;

import com.google.common.collect.Maps;

import akka.actor.ExtendedActorSystem;
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
import maquette.controller.domain.entities.project.protocol.queries.ListProjects;
import maquette.controller.domain.entities.project.protocol.results.GetProjectDetailsResult;
import maquette.controller.domain.entities.project.protocol.results.ListProjectsResult;
import maquette.controller.domain.util.databind.AbstractMessageSerializer;

public final class MessageSerializer extends AbstractMessageSerializer {

    protected MessageSerializer(ExtendedActorSystem actorSystem) {
        super(actorSystem, 2403 + 1);
    }

    @Override
    protected Map<String, Class<?>> getManifestToClass() {
        Map<String, Class<?>> m = Maps.newHashMap();

        m.put("project/commands/change-project-description/v1", ChangeProjectDescription.class);
        m.put("project/commands/change-project-owner/v1", ChangeProjectOwner.class);
        m.put("project/commands/change-project-privacy/v1", ChangeProjectPrivacy.class);
        m.put("project/commands/create-project/v1", CreateProject.class);
        m.put("project/commands/delete-project/v1", DeleteProject.class);
        m.put("project/commands/grant-project-access/v1", GrantProjectAccess.class);
        m.put("project/commands/register-dataset/v1", RegisterDataset.class);
        m.put("project/commands/remove-dataset/v1", RemoveDataset.class);
        m.put("project/commands/revoke-project-access/v1", RevokeProjectAccess.class);

        m.put("project/events/changed-project-description/v1", ChangedProjectDescription.class);
        m.put("project/events/changed-project-owner/v1", ChangedProjectOwner.class);
        m.put("project/events/changed-project-privacy/v1", ChangedProjectPrivacy.class);
        m.put("project/events/created-project/v1", CreatedProject.class);
        m.put("project/events/deleted-project/v1", DeletedProject.class);
        m.put("project/events/granted-project-access/v1", GrantedProjectAccess.class);
        m.put("project/events/registered-dataset/v1", RegisteredDataset.class);
        m.put("project/events/removed-dataset/v1", RemovedDataset.class);
        m.put("project/events/revoked-project-access/v1", RevokedProjectAccess.class);

        m.put("project/queries/get-project-details/v1", GetProjectDetails.class);
        m.put("project/queries/list-projects/v1", ListProjects.class);

        m.put("projects/results/get-project-details/v1", GetProjectDetailsResult.class);
        m.put("projects/results/list-projects/v1", ListProjectsResult.class);

        return m;
    }

}
