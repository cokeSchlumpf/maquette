package maquette.controller.domain.entities.deprecatedproject.protocol;

import java.util.Map;

import com.google.common.collect.Maps;

import akka.actor.ExtendedActorSystem;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectDescription;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectPrivacy;
import maquette.controller.domain.entities.deprecatedproject.protocol.commands.CreateProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.commands.DeleteProject;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectDescription;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectPrivacy;
import maquette.controller.domain.entities.deprecatedproject.protocol.events.CreatedProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.events.DeletedProject;
import maquette.controller.domain.entities.deprecatedproject.protocol.queries.GetProjectProperties;
import maquette.controller.domain.entities.deprecatedproject.protocol.queries.ListProjects;
import maquette.controller.domain.entities.deprecatedproject.protocol.results.GetProjectPropertiesResult;
import maquette.controller.domain.entities.deprecatedproject.protocol.results.ListProjectsResult;
import maquette.controller.domain.util.databind.AbstractMessageSerializer;

public final class MessageSerializer extends AbstractMessageSerializer {

    protected MessageSerializer(ExtendedActorSystem actorSystem) {
        super(actorSystem, 2403 + 4);
    }

    @Override
    protected Map<String, Class<?>> getManifestToClass() {
        Map<String, Class<?>> m = Maps.newHashMap();

        m.put("project/commands/project-description/v1", ChangeProjectDescription.class);
        m.put("project/commands/change-project-privacy/v1", ChangeProjectPrivacy.class);
        m.put("project/commands/create-project/v1", CreateProject.class);
        m.put("project/commands/delete-project/v1", DeleteProject.class);

        m.put("project/events/changed-project-description/v1", ChangedProjectDescription.class);
        m.put("project/events/changed-project-privacy/v1", ChangedProjectPrivacy.class);
        m.put("project/events/created-project/v1", CreatedProject.class);
        m.put("project/events/deleted-project/v1", DeletedProject.class);

        m.put("project/queries/get-project-properties/v1", GetProjectProperties.class);
        m.put("project/queries/list-projects/v1", ListProjects.class);

        m.put("project/results/get-project-properties/v1", GetProjectPropertiesResult.class);
        m.put("project/results/list-projects/v1", ListProjectsResult.class);

        return m;
    }

}
